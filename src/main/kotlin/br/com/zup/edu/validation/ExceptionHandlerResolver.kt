package br.com.zup.edu.validation

import java.lang.IllegalStateException
import javax.inject.Singleton

@Singleton
class ExceptionHandlerResolver (
     private val handlers: List<ExceptionHandler<Exception>>
        ){
//    private var defaultHandler: ExceptionHandler<Exception> = DefaultExceptionHandler()
//    constructor(handlers: List<ExceptionHandler<Exception>>, defaultHandler: ExceptionHandler<Exception>) : this(handlers){
//        this.defaultHandler = defaultHandler
//    }
//
fun resolve(e: Exception): ExceptionHandler<Exception>{
    val filtroHandles = handlers.filter { f-> f.supports(e) }
    if(filtroHandles.size >1)
        throw IllegalStateException("Too many handlers supporting the same exception")
    return filtroHandles.first()
}
}
