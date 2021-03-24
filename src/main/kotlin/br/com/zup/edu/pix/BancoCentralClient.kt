package br.com.zup.edu.pix

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.HttpResponse
import java.time.LocalDateTime

@Client("\${bcb.pix.url}")
interface BancoCentralClient {

    @Post(
        "/api/v1/pix/keys",
        produces = [MediaType.APPLICATION_XML],
        consumes = [MediaType.APPLICATION_XML]
    )
    fun create(@Body request: CreatePixKeyRequest): HttpResponse<CreatePixKeyResponse>

    @Delete(
        "/api/v1/pix/keys/{key}",
        produces = [MediaType.APPLICATION_XML],
        consumes = [MediaType.APPLICATION_XML]
    )
    fun delete(@PathVariable key: String, @Body request: DeletePixKeyRequest): HttpResponse<DeletePixKeyResponse>

    @Get(
        "/api/v1/pix/keys/{key}",
        consumes = [MediaType.APPLICATION_XML]
    )
    fun findByKey(@PathVariable key: String): HttpResponse<PixKeyDetailsResponse>
}

data class PixKeyDetailsResponse (
    val keyType: PixKeyType,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner,
    val createdAt: LocalDateTime
) {
    fun toModel(): ChavePixInfo {
        return ChavePixInfo(
            tipo = keyType.domainType!!,
            chave = this.key,
            tipoDeConta = when (this.bankAccount.accountType) {
                BankAccount.AccountType.CACC -> TipoDeConta.CONTA_CORRENTE
                BankAccount.AccountType.SVGS -> TipoDeConta.CONTA_POUPANCA
            },
            conta = ContaAssociada(
                instituicao = Instituicoes.nome(bankAccount.participant),
                nomeDoTitular = owner.name,
                cpfDoTitular = owner.taxIdNumber,
                agencia = bankAccount.branch,
                numeroDaConta = bankAccount.accountNumber
            )
        )
    }
}

data class DeletePixKeyRequest(
    val key: String,
    val participant: String = ContaAssociada.ITAU_UNIBANCO_ISPB
)

data class DeletePixKeyResponse(
    val key: String,
    val participant: String,
    val deletedAt: LocalDateTime
)

data class CreatePixKeyRequest(
    val keyType: PixKeyType,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner
){
    companion object{
        fun of(chavePix: ChavePix) : CreatePixKeyRequest{
            return CreatePixKeyRequest(
                keyType = PixKeyType.by(chavePix.tipo),
                key = chavePix.chave,
                bankAccount = BankAccount(
                    participant = ContaAssociada.ITAU_UNIBANCO_ISPB,
                    branch = chavePix.conta.agencia,
                    accountNumber = chavePix.conta.numeroDaConta,
                    accountType = BankAccount.AccountType.by(chavePix.tipoDeConta),
                ),
                owner = Owner(
                    type = Owner.OwnerType.NATURAL_PERSON,
                    name = chavePix.conta.nomeDoTitular,
                    taxIdNumber = chavePix.conta.cpfDoTitular
                )
            )
        }
    }
}

data class CreatePixKeyResponse(
    val keyType: PixKeyType,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner,
    val createdAt: LocalDateTime
)

data class Owner(
    val type: OwnerType,
    val name: String,
    val taxIdNumber: String
){
    enum class OwnerType{
        NATURAL_PERSON,
        LEGAL_PERSON
    }
}

data class BankAccount(
    val participant: String,
    val branch: String,
    val accountNumber: String,
    val accountType: AccountType
){
    enum class AccountType(){
        CACC,//Current: Account used to post debits as credits when no specific account has been nominated
        SVGS; //Savings: Savings

        companion object{
            fun by (domainType: TipoDeConta): AccountType{
                return when (domainType){
                    TipoDeConta.CONTA_CORRENTE -> CACC
                    TipoDeConta.CONTA_POUPANCA -> SVGS
                }
            }
        }
    }
}

enum class PixKeyType(val domainType: TipoDeChave?){
    CPF(TipoDeChave.CPF),
    PHONE(TipoDeChave.CELULAR),
    EMAIL(TipoDeChave.EMAIL),
    RANDOM(TipoDeChave.CHAVE_ALEATORIA);

    companion object{
        private val mapping = PixKeyType.values().associateBy(PixKeyType::domainType)

        fun by(domainType: TipoDeChave): PixKeyType{
            return mapping[domainType] ?: throw IllegalArgumentException("PixKeyType invalid")
        }
    }
}