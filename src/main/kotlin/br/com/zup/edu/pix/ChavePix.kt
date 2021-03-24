package br.com.zup.edu.pix

import java.time.LocalDateTime
import java.util.*
import javax.persistence.*
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
data class ChavePix(

    @field:NotNull
    @Column(nullable = false)
    val clienteId: UUID?,

    @field:NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val tipo: TipoDeChave,

    @field:NotBlank
    @Column(unique = true, nullable = false)
    var chave: String,

    @field:NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val tipoDeConta: TipoDeConta,

    @field:Valid
    @Embedded
    val conta: ContaAssociada,

    @Column(nullable = false)
    val criadaEm: LocalDateTime = LocalDateTime.now()
){
    @Id
    @GeneratedValue
    val id: UUID? = null


    override fun toString(): String {
        return "ChavePix(clientId=$clienteId, tipo=$tipo, chave=$chave, tipoDeConta=$tipoDeConta, conta=$conta"
    }

    fun atualiza(key: String) {
        this.chave = key
    }

    fun pertenceAo(clienteId: UUID) = this.clienteId?.equals(clienteId)
}