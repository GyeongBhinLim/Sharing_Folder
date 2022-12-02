package network.master

import io.grpc.{Server, ServerBuilder}
import org.apache.logging.log4j.scala.Logging
import java.util.concurrent.CountDownLatch
import scala.concurrent.{ExecutionContext, Future}
import scala.collection.mutable.ListBuffer

import network.common.Util.getMyIpAddress
import distributed.distributed.{
  DistributedGrpc, ConnectionCheckRequest, ConnectionCheckResponse,
  DoneRequest, DoneResponse, IsSendingDone, PartitionedDataRequest, PartitionedData, PartitionedDataResponse,
  SampleRequest, SampleResponse
}

object Master{
  def main(args: Array[String]): Unit = {
    val numClient = args.headOption
    if (numClient.isEmpty) {
      return
    }

    val server = new Master(ExecutionContext.global, numClient.get.toInt)
    server.start()
    server.printEndpoint()
    server.blockUntilShutdown()
  }

  private val port = 50051
}

class WorkerClient(val id: Int, val ip: String) {
  override def toString: String = ip
}

class Master(executionContext: ExecutionContext, val numClient: Int) extends Logging { self =>
  private[this] var server: Server = null
  private val clientLatch: CountDownLatch = new CountDownLatch(numClient)
  var slaves: Vector[WorkerClient] = Vector.empty
  var allSampleSequence: Array[String] = Array.empty

  private def start(): Unit = {
    server = ServerBuilder.forPort(Master.port).addService(DistributedGrpc.bindService(new DistributedImpl, executionContext)).build.start
    //Add Service Implementation here
    //server = ServerBuilder.forPort(Master.port).addService(FragServiceGrpc.bindService(new FragImpl, executionContext)).build.addService(SortServiceGrpc.bindService(new SortImpl, executionContext)).build.start
    logger.info("Server numClient: " + self.numClient)
    logger.info("Server started, listening on " + Master.port)
    sys.addShutdownHook {
      System.err.println("*** shutting down gRPC server since JVM is shutting down")
      self.stop()
      System.err.println("*** server shut down")
    }
  }

  private def printEndpoint(): Unit = {
    System.out.println(getMyIpAddress + ":" + Master.port)
  }

  private def stop(): Unit = {
    if (server != null) {
      server.shutdown()
    }
  }

  private def blockUntilShutdown(): Unit = {
    if (server != null) {
      server.awaitTermination(450, java.util.concurrent.TimeUnit.SECONDS)
    }
  }

  private def addNewSlave(ipAddress: String): Int = {
    this.synchronized {
      slaves foreach{ slave => if (slave.ip == ipAddress) return slave.id }
      this.slaves = this.slaves :+ new WorkerClient(this.slaves.length, ipAddress)
      if (this.slaves.length == this.numClient) printSlaveIpAddresses()
      slaves.last.id
    }
  }
  private def addSampleSequence(newSample: Array[String]): Unit = {
    this.synchronized {
      this.allSampleSequence = this.allSampleSequence.concat(newSample)
    }
  }
  private def printSlaveIpAddresses(): Unit = {
    System.out.println(this.slaves.mkString(", "))
  }

  private class DistributedImpl extends DistributedGrpc.Distributed {
    override def connectionCheck(req: ConnectionCheckRequest) = {
      val _machineID = addNewSlave(req.ipAddress)
      logger.info("sayHello from " + req.ipAddress + " with machineID: " + _machineID)
      clientLatch.countDown()
      clientLatch.await()
      print("Await is finished\n")
      val reply = ConnectionCheckResponse(machineID = _machineID)
      Future.successful(reply)
    }

    override def getSampleRange(request: SampleRequest): Future[SampleResponse] = {
      val _sampleSize = request.sampleSize
      val _sampleDataSequence = request.sampleDataSequence
      val countWorker = slaves.length
      addSampleSequence(_sampleDataSequence.toArray)
      clientLatch.countDown()
      clientLatch.await()
      print("Await is finished\n")

      //Now sort samples from all workers and calculate partition range
      allSampleSequence = allSampleSequence.sorted
      print("allSampleSequence: ")
      print(allSampleSequence)
      val rangeEachMachine = ListBuffer[(String, String)]()
      for (i <- 1 to countWorker) {
        if (i == 1)
          rangeEachMachine.append(("          ", allSampleSequence((allSampleSequence.length / countWorker) * i - 1)))
        else if (i == countWorker)
          rangeEachMachine.append((rangeEachMachine(i - 2)._2, "~~~~~~~~~~"))
        else
          rangeEachMachine.append((rangeEachMachine(i - 2)._2, allSampleSequence((allSampleSequence.length / countWorker) * i - 1)))
      }
      for (ele <- rangeEachMachine) {
        print(ele._1 + " " + ele._2 + "\n")
      }

      //sending partition range to workers
      val _rangeSequence = rangeEachMachine.toList.flatten{case (a,b)=>List(a,b)}
      val reply = SampleResponse(rangeSequence = _rangeSequence)
      Future.successful(reply)

    }

    override def requestPartitionedData(request: PartitionedDataRequest): Future[PartitionedDataResponse] = ???

    override def sendPartitionedData(request: PartitionedData): Future[IsSendingDone] = ???

    override def taskDoneReport(request: DoneRequest): Future[DoneResponse] = ???


  }

}
