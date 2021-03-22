package br.com.zup.edu.pix

import br.com.zup.edu.KeymanagerRemoveGrpcServiceGrpc
import br.com.zup.edu.RemoveChavePixRequest
import br.com.zup.edu.RemoveChavePixResponse
import br.com.zup.edu.validation.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandler
@Singleton
class RemoveChavePixEndpoint(@Inject private  val service : RemoveChavePixService) :
    KeymanagerRemoveGrpcServiceGrpc.KeymanagerRemoveGrpcServiceImplBase(){
    override fun remove(request: RemoveChavePixRequest, responseObserver: StreamObserver<RemoveChavePixResponse>) {
       service.remove(clienteId = request.clientId, pixId = request.pixId)

        responseObserver.onNext(RemoveChavePixResponse.newBuilder()
            .setClientId(request.clientId)
            .setPixId(request.pixId)
            .build())

        responseObserver.onCompleted()
    }
}