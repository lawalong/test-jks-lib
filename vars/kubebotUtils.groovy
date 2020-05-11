def deploy(region,environment,nameSpace,appName) {
    echo "333 Deploying ${appName}-wjau to ${nameSpace} ..."

    sh '''
    echo \"4442 Deploying '''+appName+''' -wjau to '''+nameSpace+''' ...\"
    url = http://kubebot.default/deploy/prod/'''+nameSpace+'''/'''+appName+'''/${BUILD_NUMBER}?registry=$CONTAINERREGISTRY&repository=webjet
    echo url
    '''

    sh(script: """
                            response=$(curl -s -X POST "http://kubebot.default/deploy/dev/${nameSpace}/${appName}-wjnz/${BUILD_NUMBER}?registry=${CONTAINERREGISTRY}")
                        
                        
                        """, returnStdout: true)
}
