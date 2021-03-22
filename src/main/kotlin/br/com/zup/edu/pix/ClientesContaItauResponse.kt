package br.com.zup.edu.pix

class ClientesContaItauResponse (val tipo: String,
                                 val instituicao: InstituicaoResponse,
                                 val agencia: String,
                                 val numero: String,
                                 val titular: TitularResponse)
{
    fun toModel(): ContaAssociada{
        return ContaAssociada(
            instituicao = this.instituicao.nome,
            nomeDoTitular = this.titular.nome,
            cpfDoTitular = this.titular.cpf,
            agencia = this.agencia,
            numeroDaConta = this.numero
        )
    }
}

class TitularResponse (
    val id: String,
    val nome: String,
    val cpf: String
        )

class InstituicaoResponse (
    val nome: String,
    val ispb: String
)
