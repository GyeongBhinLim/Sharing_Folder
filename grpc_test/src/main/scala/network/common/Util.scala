package network.common

import java.net.{DatagramSocket, InetAddress}

object Util {
  def getMyIpAddress: String = {
    val socket = new DatagramSocket
    try {
      socket.connect(InetAddress.getByName("8.8.8.8"), 10002)
      socket.getLocalAddress.getHostAddress
    } finally if (socket != null) socket.close()
  }
}
