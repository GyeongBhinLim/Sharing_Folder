package network.worker

import org.apache.logging.log4j.scala.Logging
import io.grpc.{ManagedChannel, ManagedChannelBuilder, StatusRuntimeException}
import java.util.concurrent.TimeUnit
import scala.collection.mutable.ListBuffer

import network.common.Util.getMyIpAddress
import fragment.fragment.FragServiceGrpc.FragServiceBlockingStub
import sorting.sorting.SortServiceGrpc.SortServiceBlockingStub

import fragment.fragment.
{FragServiceGrpc,
  FragRequest,
  FragReply}
import sorting.sorting.
{SortServiceGrpc,
  SortRequest,
  SortReply}


object Worker {
  def apply(host: String, port: Int): Worker = {
    val channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().asInstanceOf[ManagedChannelBuilder[_]].build
    val blockingStub = FragServiceGrpc.blockingStub(channel)
    new Worker(channel, blockingStub)
  }

  def main(args: Array[String]): Unit = {
    val masterEndpoint = args.headOption
    if (masterEndpoint.isEmpty)
      System.out.println("Master ip:port argument is empty.")
    else {
      val splitedEndpoint = masterEndpoint.get.split(':')
      val client = Worker(splitedEndpoint(0), splitedEndpoint(1).toInt)
      try {
        //add what slave should do here
        client.SayHello()
      } finally {
        client.shutdown()
      }
    }
  }
}

class Worker private(
                      private val channel: ManagedChannel,
                      private val blockingStub: FragServiceBlockingStub
                    ) extends Logging {

  def shutdown(): Unit = {
    channel.shutdown.awaitTermination(5, TimeUnit.SECONDS)
  }

  def SayHello(): Unit = {
    val wordList = List("Hello","World")

    val request = FragRequest(name = getMyIpAddress, data = wordList)
    try {
      val response = blockingStub.sayHello(request)
      logger.info("SendMessage: " + response.message)
    }
    catch {
      case e: StatusRuntimeException =>
        logger.warn(s"RPC failed: ${e.getStatus}")
    }
  }
}
