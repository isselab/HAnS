fun properties(key: String) = providers.gradleProperty(key)
rootProject.name = properties("pluginName").get()
