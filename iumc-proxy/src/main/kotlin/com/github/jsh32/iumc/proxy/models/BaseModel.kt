package com.github.jsh32.iumc.proxy.models

import io.ebean.Model
import io.ebean.annotation.WhenCreated
import io.ebean.annotation.WhenModified

import java.time.Instant
import javax.persistence.Id
import javax.persistence.MappedSuperclass

/**
 * Base class for all models in the database.
 *
 * @property id The unique identifier for the model instance.
 * @property whenModified The timestamp indicating when the model was last modified.
 * @property whenCreated The timestamp indicating when the model was created.
 */
@MappedSuperclass
open class BaseModel : Model() {
    @Id
    var id: Long = 0

    @WhenModified
    lateinit var whenModified: Instant

    @WhenCreated
    lateinit var whenCreated: Instant
}