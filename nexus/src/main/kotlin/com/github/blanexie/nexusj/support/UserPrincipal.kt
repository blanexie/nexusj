package com.github.blanexie.nexusj.support

import com.github.blanexie.nexusj.table.UserDO
import io.ktor.auth.*

data class UserPrincipal(val user: UserDO) : Principal
