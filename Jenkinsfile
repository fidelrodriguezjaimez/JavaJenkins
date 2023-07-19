pipeline {
  agent any
  stages {
    stage('Git CheckOut') {
      agent any
      steps {
        git(url: 'https://github.com/fidelrodriguezjaimez/JavaJenkins.git', branch: 'develop')
        echo 'CheckOut realizado con exito'
      }
    }

    stage('Build') {
      steps {
        sh 'mvn clean install -U'
        echo 'Compilacion exitosa'
      }
    }
    
    stage('Test') {
      steps {
        sh 'mvn test'
        echo 'Pruebas unitarias exitosas'
      }
    }

    stage('SonarQube Scan') {
      steps {
        checkout scm
        sh "mvn package sonar:sonar \
        -Dsonar.projectKey=${SONAR_KEY} \
        -Dsonar.projectName=${SONAR_KEY} \
        -Dsonar.sources=src/main \
        -Dsonar.host.url=${SONAR_SERVER} \
        -Dsonar.login=${SONAR_TOKEN} \
        -Dsonar.branch.name=${BRANCH_NAME}"
        
        echo 'Scaneo Exitoso'
      }
    }

    stage('BuildImage') {
      steps {
        sh 'docker build -t fidelrdgzjmz/java-imagen:${BUILD_NUMBER} .'       
        echo 'Build Image succes'
      }
    }
    /*
    stage('Push DockerHub') {
      steps {
        sh 'echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin'
        sh 'docker push fidelrdgzjmz/java-imagen:${BUILD_NUMBER}'
        echo 'Push Image succes'
      }
    } */   

    stage("Deep Security Smart Check scan") {
      steps {
        smartcheckScan([
            imageName: "fidelrdgzjmz/java-imagen:${BUILD_NUMBER}",
            smartcheckHost: "container.us-1.cloudone.trendmicro.com",
            smartcheckCredentialsId: "smartcheck-auth",
            preregistryScan: true,
        ])
      }
    }

    stage('Push Harbor') {
      steps {
        sh 'docker tag fidelrdgzjmz/java-imagen:${BUILD_NUMBER} demo.goharbor.io/jenkinsjavaimage/java-imagen:${BUILD_NUMBER}'
        sh 'docker login ${HARBOR_URL} -u ${HARBOR_USERNAME} -p ${HARBOR_PASSWORD}'
        sh 'docker push ${HARBOR_URL}/jenkinsjavaimage/java-imagen:${BUILD_NUMBER}'
        sh 'docker rmi ${HARBOR_URL}/jenkinsjavaimage/java-imagen:${BUILD_NUMBER}'
        sh 'docker rmi java-imagen:${BUILD_NUMBER}'        
        sh 'docker images'
        echo 'Image Push succed'
      }
    }

    stage('Funcional Tests') {
      steps {
        sh '''#!/bin/bash          
          cd testing/funcional/develop
          newman run Petstore.postman_collection.json -e Petstorestaging.postman_environment.json -r junit,cli --reporter-junit-export result-tests-staging.xml'''
        echo 'scripts funcionales ejecutados exitosamente'
      }
    }
    
    stage('Performance Tests') {
      steps {
        sh '''#!/bin/bash
          cd testing/stress/develop
          mvn clean install -U
          mvn gatling:test -o'''
        echo 'scripts de carga ejecutados exitosamente'
      }
    }
    
  }
  environment {
    SONAR_KEY = '23_Coppel_TestJenkinsGitHub'
    SONAR_SERVER = 'https://devtools.axity.com/sonarlts'
    SONAR_TOKEN = credentials('sonartoken-secret')
    HARBOR_URL = 'demo.goharbor.io'
    HARBOR_USERNAME = 'fidel.rodriguez'
    HARBOR_PASSWORD = credentials('harborpas-secret')
    DOCKERHUB_CREDENTIALS = credentials('dockerhub')
  }
  post {
    success {
      echo 'Esto se ejecutará solo si se ejecuta correctamente'
    }

    failure {
      echo 'Esto se ejecutará solo si falla'
    }

  }
}
