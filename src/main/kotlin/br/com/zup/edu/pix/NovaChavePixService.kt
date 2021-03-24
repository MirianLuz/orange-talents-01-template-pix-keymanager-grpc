package br.com.zup.edu.pix

import br.com.zup.edu.validation.ChavePixExistenteException
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import java.lang.IllegalStateException
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class NovaChavePixService(
    @Inject val chavePixRepository: ChavePixRepository,
    @Inject val clientesContaItauClient: ClientesContaItauClient,
    @Inject val bcbClient: BancoCentralClient
) {

    private val Logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun registra(@Valid novaChavePix: NovaChavePix): ChavePix {

        //1. Verifica se a chave já existe no sistema
        if (chavePixRepository.existsByChave(novaChavePix.chave)) 
            throw ChavePixExistenteException("Chave Pix ${novaChavePix.chave} existente")
        

        //2. busca dados da conta no ERP do itau
        val response = clientesContaItauClient.buscaContaPorTipo(novaChavePix.clientId!!, novaChavePix.tipoDeConta!!.name)

        val conta = response.body()?.toModel() ?: throw IllegalStateException("Cliente não encontrado no Itaú")

        //3. salva no banco de dados
        val chave = novaChavePix.toModel(conta)
        chavePixRepository.save(chave)

        //4. registra chave no BCB
        val bcbRequest = CreatePixKeyRequest.of(chave).also{
            Logger.info("Registrando chave Pix no Banco Central do Brasil(BCB): $it")
        }

        val bcbResponse = bcbClient.create(bcbRequest)
        if (bcbResponse.status != HttpStatus.CREATED) {
            throw IllegalStateException("Ocorreu um erro ao registrar a sua chave pix no Banco Central")

        }

        // 5. atualiza chave do dominio com chave gerada pelo BCB
        chave.atualiza(bcbResponse.body()!!.key)

        return chave
    }
}