plugins {
    id("com.cognifide.environment")
    id("com.cognifide.aem.instance.local")
}

repositories {
    jcenter()
    maven("https://dl.bintray.com/acs/releases")
}

aem {
    `package` {
        validator { // https://github.com/Cognifide/gradle-aem-plugin/blob/master/docs/package-plugin.md#crx-package-validation
            base("com.adobe.acs:acs-aem-commons-oakpal-checks:4.4.0")
        }
    }

    instance {
        satisfier { // https://github.com/Cognifide/gradle-aem-plugin/blob/master/docs/instance-plugin.md#task-instancesatisfy
            packages {
                // "dep.vanity-urls"("pkg/vanityurls-components-1.0.2.zip")
                "dep.core-components-all"("com.adobe.cq:core.wcm.components.all:2.8.0@zip")
                "dep.core-components-examples"("com.adobe.cq:core.wcm.components.examples:2.8.0@zip")
                "tool.aem-easy-content-upgrade"("https://github.com/valtech/aem-easy-content-upgrade/releases/download/3.1.0/aecu.bundle-3.1.0.zip")
                "tool.ac-tool.package"("https://repo1.maven.org/maven2/biz/netcentric/cq/tools/accesscontroltool/accesscontroltool-package/2.3.2/accesscontroltool-package-2.3.2.zip")
                "tool.ac-tool.oak-index"("https://repo1.maven.org/maven2/biz/netcentric/cq/tools/accesscontroltool/accesscontroltool-oakindex-package/2.3.2/accesscontroltool-oakindex-package-2.3.2.zip")
                "tool.search-webconsole-plugin"("com.neva.felix:search-webconsole-plugin:1.3.0")
            }
        }
        provisioner {
            // https://github.com/Cognifide/gradle-aem-plugin/blob/master/docs/instance-plugin.md#task-instanceprovision
        }
    }

    localInstance {
        install {
            files {
                // https://github.com/Cognifide/gradle-aem-plugin/blob/master/docs/local-instance-plugin.md#pre-installed-osgi-bundles-and-crx-packages
            }
        }
    }
}

environment { // https://github.com/Cognifide/gradle-environment-plugin
    docker {
        containers {
            "httpd" {
                resolve {
                    resolveFiles {
                        download("http://download.macromedia.com/dispatcher/download/dispatcher-apache2.4-linux-x86_64-4.3.3.tar.gz").use {
                            copyArchiveFile(it, "**/dispatcher-apache*.so", file("modules/mod_dispatcher.so"))
                        }
                    }
                    rootProject.file("env/src/environment/httpd/conf.d/variables/default.vars")
                            .copyTo(rootProject.file("app/aem/dispatcher/src/conf.d/variables/default.vars"), true)
                    ensureDir("htdocs", "cache", "logs")
                }
                up {
                    ensureDir("/usr/local/apache2/logs", "/var/www/localhost/htdocs", "/var/www/localhost/cache")
                    execShell("Starting HTTPD server", "/usr/sbin/httpd -k start")
                }
                reload {
                    cleanDir("/var/www/localhost/cache")
                    execShell("Restarting HTTPD server", "/usr/sbin/httpd -k restart")
                }
                dev {
                    watchRootDir(
                            "app/aem/dispatcher/src/conf.d",
                            "app/aem/dispatcher/src/conf.dispatcher.d",
                            "env/src/environment/httpd")
                }
            }
        }
    }
    hosts {
        "http://example.com" { tag("live") }
    }

    healthChecks {
        http("Site 'live'", "http://example.com", "English - United States")
        http("Author Sites Editor", "http://localhost:4502/sites.html") {
            containsText("Sites")
            options { basicCredentials = aem.authorInstance.credentials }
        }
    }
}

tasks {
    instanceResolve {
        dependsOn(":requireProps")
    }
    instanceCreate {
        dependsOn(":requireProps")
    }
    environmentUp {
        mustRunAfter(instanceUp, instanceSatisfy, instanceProvision, instanceSetup)
    }
    environmentAwait {
        mustRunAfter(instanceAwait)
    }
}
