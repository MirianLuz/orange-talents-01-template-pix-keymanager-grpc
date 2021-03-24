package br.com.zup.edu.pix

import br.com.zup.edu.CarregaChavePixRequest
import br.com.zup.edu.CarregaChavePixResponse
import br.com.zup.edu.KeymanagerCarregaGrpcServiceGrpc
import br.com.zup.edu.pix.BancoCentralClient
import br.com.zup.edu.validation.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.Validator

@ErrorHandler
@Singleton
class CarregaChaveEndpoint(
    @Inject private val repository: ChavePixRepository,
    @Inject private val bcbClient: BancoCentralClient,
    @Inject private val validator: Validator,
) : KeymanagerCarregaGrpcServiceGrpc.KeymanagerCarregaGrpcServiceImplBase() {

    override fun carrega(
        request: CarregaChavePixRequest,
        responseObserver: StreamObserver<CarregaChavePixResponse>, // 1
    ) {

        val filtro = request.toModel(validator)
        val chaveInfo = filtro.filtra(repository = repository, bcbClient = bcbClient)

        responseObserver.onNext(CarregaChavePixResponseConverter().convert(chaveInfo))
        responseObserver.onCompleted()
    }
}