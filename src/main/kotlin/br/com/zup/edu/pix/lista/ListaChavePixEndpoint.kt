package br.com.zup.edu.pix.lista

import br.com.zup.edu.*
import br.com.zup.edu.pix.BancoCentralClient
import br.com.zup.edu.pix.ChavePixRepository
import br.com.zup.edu.pix.TipoDeChave
import br.com.zup.edu.pix.TipoDeConta
import br.com.zup.edu.validation.ErrorHandler
import com.google.protobuf.Timestamp
import io.grpc.stub.StreamObserver
import java.time.ZoneId
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.Validator

@ErrorHandler
@Singleton
class ListaChavePixEndpoint(@Inject private val repository: ChavePixRepository)
    : KeymanagerListaGrpcServiceGrpc.KeymanagerListaGrpcServiceImplBase()
{
    override fun lista(
        request: ListaChavePixRequest,
        responseObserver: StreamObserver<ListaChavePixResponse>
    ) {
       if (request.clientId.isNullOrBlank())
           throw IllegalArgumentException("Cliente ID não pode ser nulo ou vazio")

       val clienteId = UUID.fromString(request.clientId)
       val chaves = repository.findAllByClienteId(clienteId).map{
           ListaChavePixResponse.ChavePix.newBuilder()
               .setPixId(it.id.toString())
               .setTipoChave(TipoChave.valueOf(it.tipo.name))
               .setChave(it.chave)
               .setTipoConta(TipoConta.valueOf(it.tipoDeConta.name))
               .setCriadaEm(it.criadaEm.let {
                   val createdAt = it.atZone(ZoneId.of("UTC")).toInstant()
                   Timestamp.newBuilder()
                       .setSeconds(createdAt.epochSecond)
                       .setNanos(createdAt.nano)
                       .build()
               })
               .build()
       }

        responseObserver.onNext(ListaChavePixResponse.newBuilder()
            .setClienteId(clienteId.toString())
            .addAllChaves(chaves)
            .build())
        responseObserver.onCompleted()
    }
}