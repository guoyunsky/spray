/*
 * Copyright (C) 2011-2012 spray.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package spray.util

import com.typesafe.config.{ConfigFactory, Config}
import akka.actor._


class UtilSettings(config: Config) {
  private val c = ConfigUtils.prepareSubConfig(config, "spray.util")

  val LogActorPathsWithDots = c getBoolean "log-actor-paths-with-dots"
  val LogActorSystemName    = c getBoolean "log-actor-system-name"
}

class UtilSettingsExtension(system: ExtendedActorSystem) extends Extension {
  @volatile private[this] var _settings: UtilSettings = _

  def get: UtilSettings = {
    if (_settings == null) _settings = new UtilSettings(system.settings.config)
    _settings
  }

  def set(settings: UtilSettings) {
    _settings = settings
  }
}

object UtilSettings extends ExtensionId[UtilSettingsExtension] with ExtensionIdProvider {
  def lookup() = UtilSettings
  def createExtension(system: ExtendedActorSystem) = new UtilSettingsExtension(system)

  def update(system: ActorSystem, settings: UtilSettings) {
    apply(system).set(settings)
  }

  /**
   * Global settings initialized from the default application configuration by default.
   * You can either set custom settings or enable per-ActorSystem configuration by setting
   * `UtilSettings.global = None`. The latter will load the `spray.util` settings from
   * the configuration of the ActorSystem or allow for directly specifying
   * per-ActorSystem settings with `UtilSettings(system) = new UtilSettings(...)`.
   */
  @volatile var global: Option[UtilSettings] = Some(new UtilSettings(ConfigFactory.load()))
}