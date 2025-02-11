package br.com.zup.edu.pix

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client

@Client("\${itau.contas.url}")
interface ClientesContaItauClient {

    @Get("/api/v1/clientes/{clienteId}/contas{?tipo}")
    fun buscaContaPorTipo(@QueryValue clienteId: String, @QueryValue tipo: String) : HttpResponse<ClientesContaItauResponse>
}