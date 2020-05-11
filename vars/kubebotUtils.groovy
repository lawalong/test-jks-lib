def deploy(region,environment,nameSpace,appName) {
    echo "333 Deploying ${appName}-wjau to ${nameSpace} ..."

    sh '''
    echo \"4442 Deploying '''+appName+''' -wjau to '''+nameSpace+''' ...\"
    '''

    sh(script: """
                            response=$(curl -s -X POST "http://kubebot.default/deploy/dev/${nameSpace}/${appName}-wjnz/${BUILD_NUMBER}?registry=${CONTAINERREGISTRY}&repository=webjet" \
                            --data-binary "@$WORKSPACE/pipeline/deploy.yaml" \
                            -H 'Content-Type: application/yaml' \
                            -H 'Expect:' \
                            -D -)
                        
                        
                        """, returnStdout: true)
}
