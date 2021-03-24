package br.com.zup.edu.pix.remove

import br.com.zup.edu.pix.BancoCentralClient
import br.com.zup.edu.validation.ChavePixNaoEncontradaException
import br.com.zup.edu.pix.ChavePixRepository
import br.com.zup.edu.pix.DeletePixKeyRequest
import br.com.zup.edu.validation.ValidUUID
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.constraints.NotBlank

@Validated
@Singleton
class RemoveChavePixService(@Inject val repository: ChavePixRepository,
                            @Inject val bcbClient: BancoCentralClient) {

    @Transactional
    fun remove(
        @NotBlank @ValidUUID(message = "cliente ID com o formato inválido") clienteId: String?,
        @NotBlank @ValidUUID(message = "pix ID com o formato inválido") pixId: String?
    ){

        val uuidPixId = UUID.fromString(pixId)
        val uuidClienteId = UUID.fromString(clienteId)

        val chave = repository.findByIdAndClienteId(uuidPixId, uuidClienteId)
            .orElseThrow{ ChavePixNaoEncontradaException("Chave Pix não encontrada") }

        repository.deleteById(uuidPixId)

        val request = DeletePixKeyRequest(chave.chave)

        val bcbResponse = bcbClient.delete(key = chave.chave, request = request)
        if(bcbResponse.status != HttpStatus.OK){
            throw IllegalStateException("Erro ao remover chave Pix no BCB")
        }

    }

}
