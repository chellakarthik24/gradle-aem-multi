import com.cognifide.gradle.aem.common.instance.local.Source
import com.neva.gradle.fork.ForkExtension

configure<ForkExtension> {
    properties {
        define("Project specific", mapOf(
                "instanceAuthor" to {
                    label = "Author Instance Only"
                    description = "Allows to temporarily limit instances to work with to author instance only."
                    checkbox(false)
                },
                "packageDamAssetToggle" to {
                    label = "DAM Asset Workflows Toggling"
                    description = "Allows to temporarily disable DAM assets processing only for time of CRX package deployment.\n" +
                            "Useful to avoid redundant DAM rendition generation when package already contains all renditions (synchronized earlier)."
                    checkbox(true)
                },
                "webpackMode" to {
                    label = "Webpack Build Mode"
                    description = "Controls used set of configuration (use 'prod' for minified source code, 'dev' - not minified)"
                    select("dev", "prod")
                }
        ))

        define("File transfer", mapOf(
                "companyUser" to {
                    label = "User"
                    description = "Authorized to access AEM files"
                    defaultValue = System.getProperty("user.name").orEmpty()
                    optional()
                },
                "companyPassword" to {
                    label = "Password"
                    description = "For above user"
                    optional()
                },
                "companyDomain" to {
                    label = "Domain"
                    description = "Needed only when accessing AEM files over SMB"
                    defaultValue = System.getenv("USERDOMAIN").orEmpty()
                    optional()
                }
        ))

        define("Instance type", mapOf(
                "instanceType" to {
                    label = "Type"
                    select("local", "remote")
                    description = "Local - instance will be created on local file system\nRemote - connecting to remote instance only"
                    controller { toggle(value == "local", "instanceRunModes", "instanceJvmOpts", "localInstance*") }
                },
                "instanceAuthorHttpUrl" to {
                    label = "Author HTTP URL"
                    url("http://localhost:4502")
                    optional()
                    description = "For accessing AEM author instance (leave empty to do not use it)"
                },
                "instancePublishHttpUrl" to {
                    label = "Publish HTTP URL"
                    url("http://localhost:4503")
                    optional()
                    description = "For accessing AEM publish instance (leave empty to do not use it)"
                }
        ))

        define("Local instance", mapOf(
                "localInstanceSource" to {
                    label = "Source"
                    description = "Controls how instances will be created (from scratch, backup or any available source)"
                    select(Source.values().map { it.name.toLowerCase() }, Source.AUTO.name.toLowerCase())
                },
                "localInstanceQuickstartJarUri" to {
                    label = "Quickstart URI"
                    description = "For file named 'cq-quickstart-x.x.x.jar'"
                },
                "localInstanceQuickstartLicenseUri" to {
                    label = "Quickstart License URI"
                    description = "For file named 'license.properties'"
                },
                "localInstanceBackupDownloadUri" to {
                    label = "Backup Download URI"
                    description = "For backup file. Protocols supported: SMB/SFTP/HTTP"
                    optional()
                },
                "localInstanceBackupUploadUri" to {
                    label = "Backup Upload URI"
                    description = "For directory containing backup files. Protocols supported: SMB/SFTP"
                    optional()
                },
                "instanceRunModes" to {
                    label = "Run Modes"
                    text("local")
                },
                "instanceJvmOpts" to {
                    label = "JVM Options"
                    text("-server -Xmx2048m -XX:MaxPermSize=512M -Djava.awt.headless=true")
                }
        ))

        define("Other", mapOf(
                "notifierEnabled" to {
                    label = "Notifications"
                    description = "Controls displaying of GUI notifications (baloons)"
                    checkbox(true)
                }
        ))
    }
}
