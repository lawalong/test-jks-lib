def deploy(region,environment,nameSpace,appName) {
    echo "333 Deploying ${appName}-wjau to ${nameSpace} ..."

    sh(script: """
    echo "4442 Deploying ${appName}-wjau to ${nameSpace} ..."

                        """, returnStdout: true) 
}
