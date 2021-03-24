package br.com.zup.edu.pix

import io.micronaut.context.annotation.Executable
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface ChavePixRepository : JpaRepository<ChavePix, UUID>{

    @Executable
    fun existsByChave(chave: String?): Boolean

    fun findByIdAndClienteId(uuidPixId: UUID?, uuidClienteId: UUID?): Optional<ChavePix>

    fun findByChave(chave: String): Optional<ChavePix>
}