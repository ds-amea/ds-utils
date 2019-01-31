pipeline {
   agent any
   environment {
          DSM_URL=""
          DS_API_KEY=""
          SEARCH_NAME=""
    }
    stages
    {
        stage('Checkout') { // for display purposes
           steps{
                sh 'printenv'
                checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'ds-devsecops']], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/tsheth/DSSC-DevSecOps.git']]])
            }
        }
        stage('Turnoff Application Control') { // for display purposes
           steps{
                sh (
                    script: "python3 ds-devsecops/appControl-pipeline.py  --app_control_status='off' --search_name=$SEARCH_NAME --ds_api_key=$DS_API_KEY --ds_url=$DSM_URL",
                    returnStdout: true
                )
                echo "Turning application control off, so new release application can be whitelisted."
            }
        }
        stage('Build with Maven') {
            steps{
                // sh("docker build -t $SCAN_REGISTRY/$JOB_BASE_NAME:$BUILD_ID $WORKSPACE/smart-check")
                echo "Building application"
                sh("sleep 30")
            }
        }
        stage('EC2 OS Vulnerability scan') {
              steps{
                  script{
                      // sh("eval \$(aws ecr get-login --region us-east-2 --no-include-email | sed 's|https://||')")
                      // sh("docker push $SCAN_REGISTRY/$JOB_BASE_NAME:$BUILD_ID")
                      echo "Deep security recommendation scan initiated for virtual patching."
                      sh("sleep 30")
                  }
            }
        }
        stage('Certify Release')
        {
            steps{
                // sh ("docker tag $SCAN_REGISTRY/$JOB_BASE_NAME:$BUILD_ID $SCAN_REGISTRY/$JOB_BASE_NAME:latest")
                echo "send notification to product team lead"
                sh("sleep 30")
            }
        }
        stage('Deploy to Production')
        {
            steps{
                //sh ("docker push $SCAN_REGISTRY/$JOB_BASE_NAME:latest")
                echo "Deployed code to production"
                sh("sleep 30")
            }
            
        }
        stage('TurnOn Application Control') { // for display purposes
           steps{
                sh (
                    script: "python3 ds-devsecops/appControl-pipeline.py  --app_control_status='on' --search_name=$SEARCH_NAME --ds_api_key=$DS_API_KEY --ds_url=$DSM_URL",
                    returnStdout: true
                )
            }
        }
    }
}
