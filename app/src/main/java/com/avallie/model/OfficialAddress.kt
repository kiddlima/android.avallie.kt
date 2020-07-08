package com.avallie.model

import java.io.Serializable

class OfficialAddress(
    var cep: String?,
    var logradouro: String?,
    var complemento: String?,
    var bairro: String?,
    var localidade: String?,
    var uf: String?,
    var unidade: String?
) : Serializable