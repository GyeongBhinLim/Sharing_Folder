package network.worker

import distributed.distributed.DistributedGrpc.{DistributedBlockingClient, DistributedBlockingStub}
import org.apache.logging.log4j.scala.Logging
import io.grpc.{ManagedChannel, ManagedChannelBuilder, StatusRuntimeException}

import java.util.concurrent.TimeUnit
import scala.concurrent.{Future, Promise}
import network.common.Util.getMyIpAddress
import network.common.Phase
import fragment.Fragmentation
import sorting.Sort
import distributed.distributed.{ConnectionCheckRequest, ConnectionCheckResponse, DistributedGrpc, DoneRequest, DoneResponse, IsSendingDone, PartitionedData, PartitionedDataRequest, PartitionedDataResponse, SampleRequest, SampleResponse}

import scala.collection.mutable.ListBuffer
import scala.concurrent.Promise


object Worker {
  def apply(host: String, port: Int): Worker = {
    val channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().asInstanceOf[ManagedChannelBuilder[_]].build
//    val blockingStub = FragServiceGrpc.blockingStub(channel)
    val stub = DistributedGrpc.blockingStub(channel)
    new Worker(channel, stub)
  }

  def main(args: Array[String]): Unit = {
    var currentPhase = Phase.INITIAL
    val masterEndpoint = args.headOption
    if (masterEndpoint.isEmpty) {
      System.out.println("Master ip:port argument is empty.")
      System.exit(1)
    }
    val splitedEndpoint = masterEndpoint.get.split(':')
    val client = Worker(splitedEndpoint(0), splitedEndpoint(1).toInt)
    try {
      //add what slave should do here
      client.connectionCheck()
      client.getSampleRange()
      //client.fragmentation.splitFile("/home/indigo/genLarge")
      // FIX: Fix phase
      currentPhase = Phase.TERMINATING
    } finally {
      client.shutdown()
    }
  }
}

class Worker private(
                      private val channel: ManagedChannel,
                      private val blockingStub: DistributedGrpc.DistributedBlockingStub
                    ) extends Logging {
  var machineID = -1;
  val fragmentation = new Fragmentation
  val sort = new Sort
  var PartitionRanges = Array[String]()

  def shutdown(): Unit = {
    channel.shutdown.awaitTermination(5, TimeUnit.SECONDS)
  }

  /*
  *
  * */
  def connectionCheck(): Unit = {
    println(getMyIpAddress)
    val request = ConnectionCheckRequest(ipAddress = getMyIpAddress)
    try {
      fragmentation.splitFile("/home/indigo/genLarge")
      val fileCount = fragmentation.getFileCount()
      for (i <- 1 to fileCount) {
        sort.sortFile("partition." + i.toString)
      }
      val response = blockingStub.connectionCheck(request)
      machineID = response.machineID
      logger.info("Fragmentation and Sampling Done: " + response.machineID)
    }
    catch {
      case e: StatusRuntimeException =>
        logger.warn(s"RPC failed: ${e.getStatus}")
    }
    //response
  }

  def getSampleRange(): Unit = {
    println(getMyIpAddress)
    //sampling
    val sampleElements = new ListBuffer[String]()
    for (i <- 1 to fragmentation.getFileCount()) {
      sampleElements.appendAll(sort.samplingFile("partition." + i.toString))
    }
    // Scalapb does not support scala.ListBuffer it has to be converted into List.
    val toMaster = sampleElements.toList.sorted

    // You can set the size of sample at the Sort.class
    val request = SampleRequest(sampleSize = sort._sampleSize ,sampleDataSequence = toMaster)
    try {
      val response = blockingStub.getSampleRange(request)
      PartitionRanges = PartitionRanges.concat(response.rangeSequence.toArray)
      for (i <- 0 to response.rangeSequence.toList.length - 1) {
        logger.info("PartitionRange[$i]: " + PartitionRanges(i))
      }
    }
    catch {
      case e: StatusRuntimeException =>
        logger.warn(s"RPC failed: ${e.getStatus}")
    }
    //response
  }

  def requestPartitionedData(request: PartitionedDataRequest): Future[PartitionedDataResponse] = ???

  def sendPartitionedData(request: PartitionedData): Future[IsSendingDone] = ???

  def taskDoneReport(request: DoneRequest): Future[DoneResponse] = ???
}