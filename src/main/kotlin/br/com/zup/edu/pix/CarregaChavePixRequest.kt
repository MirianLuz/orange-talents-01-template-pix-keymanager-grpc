package br.com.zup.edu.pix

import br.com.zup.edu.CarregaChavePixRequest
import br.com.zup.edu.CarregaChavePixRequest.FiltroCase.*
import br.com.zup.edu.pix.Filtro
import javax.validation.ConstraintViolationException
import javax.validation.Validator

fun CarregaChavePixRequest.toModel(validator: Validator): Filtro {

    val filtro = when(filtroCase) {
        PIXID -> pixId.let { // 1
            Filtro.PorPixId(clienteId = it.clientId, pixId = it.pixId)
        }
        CHAVE -> Filtro.PorChave(chave)
        FILTRO_NOT_SET -> Filtro.Invalido()
    }

    val violations = validator.validate(filtro)
    if (violations.isNotEmpty()) {
        throw ConstraintViolationException(violations);
    }

    return filtro
}