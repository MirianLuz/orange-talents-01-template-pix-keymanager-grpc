package br.com.zup.edu.pix

import br.com.zup.edu.validation.ChavePixExistenteException
import io.micronaut.validation.Validated
import java.lang.IllegalStateException
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class NovaChavePixService(
    @Inject val chavePixRepository: ChavePixRepository,
    @Inject val clientesContaItauClient: ClientesContaItauClient
) {
    @Transactional
    fun registra(@Valid novaChavePix: NovaChavePix): ChavePix {

        //1 Verifica se a chave já existe no sistema
        if (chavePixRepository.existsByChave(novaChavePix.chave)) 
            throw ChavePixExistenteException("Chave Pix ${novaChavePix.chave} existente")
        

        //2 busca dados da conta no ERP do itau
        val response = clientesContaItauClient.buscaContaPorTipo(novaChavePix.clientId!!, novaChavePix.tipoDeConta!!.name)

        val conta = response.body()?.toModel() ?: throw IllegalStateException("Cliente não encontrado no Itaú")

        //3 salva no banco de dados
        val chave = novaChavePix.toModel(conta)
        chavePixRepository.save(chave)

        return chave
    }
}