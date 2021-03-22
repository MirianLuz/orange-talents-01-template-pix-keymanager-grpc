package br.com.zup.edu.pix

import br.com.zup.edu.RegistraChavePixRequest
import br.com.zup.edu.TipoChave
import br.com.zup.edu.TipoConta


fun RegistraChavePixRequest.toModel(): NovaChavePix {
    return NovaChavePix(
       clientId = clientId,
        tipo = when(tipoChave){
            TipoChave.UNKNOW_KEY_TYPE -> null
            else -> TipoDeChave.valueOf(tipoChave.name)
                                   }
        ,
        chave = chave,
        tipoDeConta = when(tipoConta){
            TipoConta.UNKNOW_ACCOUNT_TYPE -> null
            else -> br.com.zup.edu.pix.TipoDeConta.valueOf(tipoConta.name)
        }
    )
}