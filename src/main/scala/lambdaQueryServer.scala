
import com.hellogrpc.hello.{lambdaInvoke, lambdaQueryGrpc, lambdaResponse}

import scala.concurrent.Future

object lambdaQueryServer {
  def 
}






class loqQueryImpl extends lambdaQueryGrpc.lambdaQuery {
  override def checkLogBool(req: lambdaInvoke) ={
    val response = lambdaResponse(message = "TRUE " + req.lowerDate + " " + req.upperDate)
    Future.successful(response)
  }

}