package br.com.zup.edu.pix.carrega

import br.com.zup.edu.pix.BancoCentralClient
import br.com.zup.edu.pix.ChavePixInfo
import br.com.zup.edu.validation.ChavePixNaoEncontradaException
import br.com.zup.edu.pix.ChavePixRepository
import br.com.zup.edu.validation.ValidUUID
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.HttpStatus
import org.slf4j.LoggerFactory
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Introspected
sealed class Filtro {

    abstract fun filtra(repository: ChavePixRepository, bcbClient: BancoCentralClient): ChavePixInfo

    @Introspected
    data class PorPixId(
        @field:NotBlank @field:ValidUUID val clienteId: String, // 1
        @field:NotBlank @field:ValidUUID val pixId: String,
    ) : Filtro() { // 1

        fun pixIdAsUuid() = UUID.fromString(pixId)
        fun clienteIdAsUuid() = UUID.fromString(clienteId)

        override fun filtra(repository: ChavePixRepository, bcbClient: BancoCentralClient): ChavePixInfo {
            return repository.findById(pixIdAsUuid())
                .filter { it.pertenceAo(clienteIdAsUuid())!! }
                .map(ChavePixInfo.Companion::of)
                .orElseThrow { ChavePixNaoEncontradaException("Chave Pix não encontrada") }
        }
    }

    @Introspected
    data class PorChave(@field:NotBlank @Size(max = 77) val chave: String) : Filtro() { // 1

        private val LOGGER = LoggerFactory.getLogger(this::class.java)

        override fun filtra(repository: ChavePixRepository, bcbClient: BancoCentralClient): ChavePixInfo {
            return repository.findByChave(chave)
                .map(ChavePixInfo.Companion::of)
                .orElseGet {
                    LOGGER.info("Consultando chave Pix '$chave' no Banco Central do Brasil (BCB)")

                    val response = bcbClient.findByKey(chave) // 1
                    when (response.status) { // 1
                        HttpStatus.OK -> response.body()?.toModel() // 1
                        else -> throw ChavePixNaoEncontradaException("Chave Pix não encontrada") // 1
                    }
                }
        }
    }

    @Introspected
    class Invalido() : Filtro(){
        override fun filtra(repository: ChavePixRepository, bcbClient: BancoCentralClient): ChavePixInfo {
            throw IllegalArgumentException("Chave Pix Inválida ou não informada")
        }
    }
}
