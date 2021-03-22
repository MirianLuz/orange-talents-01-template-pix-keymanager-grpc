package br.com.zup.edu.pix

import br.com.zup.edu.validation.ChavePixNaoEncontradaException
import br.com.zup.edu.validation.ValidUUID
import io.micronaut.validation.Validated
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.constraints.NotBlank

@Validated
@Singleton
class RemoveChavePixService(@Inject val repository: ChavePixRepository) {

    @Transactional
    fun remove(
        @NotBlank @ValidUUID(message = "cliente ID com o formato inválido") clienteId: String?,
        @NotBlank @ValidUUID(message = "pix ID com o formato inválido") pixId: String?
    ){

        val uuidPixId = UUID.fromString(pixId)
        val uuidClientId = UUID.fromString(clienteId)

        val chave = repository.findByIdAndClientId(uuidPixId, uuidClientId)
            .orElseThrow{ ChavePixNaoEncontradaException("Chave Pix não encontrada") }

        repository.deleteById(uuidPixId)

    }

}