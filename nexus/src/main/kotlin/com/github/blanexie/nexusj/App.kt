package com.github.blanexie.nexusj

import cn.hutool.core.io.resource.ClassPathResource
import cn.hutool.setting.Setting
import com.github.blanexie.dao.UserDO
import com.github.blanexie.nexusj.controller.auth
import com.github.blanexie.nexusj.controller.notAuth
import com.github.blanexie.nexusj.support.SimpleJWT
import com.github.blanexie.nexusj.support.UserPrincipal
import com.github.blanexie.nexusj.support.gson
import com.github.blanexie.nexusj.support.jwtDecode
import com.github.blanexie.tracker.server.tracker
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.routing.*
import java.text.DateFormat
import java.time.LocalDateTime


val setting = Setting(ClassPathResource(System.getProperty("properties.path") ?: "app.properties").path)

fun main(args: Array<String>): Unit {
    io.ktor.server.netty.EngineMain.main(args)
}


fun Application.nexus(testing: Boolean = true) {
    val simpleJWT = SimpleJWT()
    install(AutoHeadResponse)
    install(Compression)
    install(CallLogging)
    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
        }
    }
    install(Authentication) {
        jwt {
            verifier(simpleJWT.verifier)
            this.realm = simpleJWT.realm()
            validate { credential ->
                if (credential.payload.audience.contains(simpleJWT.audience())) {
                    val subject = credential.payload.subject
                    val jwtDecode = jwtDecode(subject)
                    val fromJson = (gson.fromJson(jwtDecode, Map::class.java)["h"] as Map<*, *>)["values"] as Map<*, *>
                    val userDO = UserDO()
                    userDO.id = fromJson["id"] as Int
                    userDO.nick = fromJson["nick"] as String
                    userDO.email = fromJson["email"] as String
                    userDO.authKey = fromJson["authKey"] as String
                    userDO.createTime = fromJson["createTime"] as LocalDateTime
                    userDO.sex = fromJson["sex"] as Int
                    UserPrincipal(userDO)
                } else {
                    null
                }
            }
        }
    }

    routing {
        route(("/")) {
            tracker()
        }
        route(("/api/nexus")) {
            authenticate {
                auth()
            }
            notAuth()
        }
    }
}

