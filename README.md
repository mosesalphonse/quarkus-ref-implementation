# quarkus-ref-implementation

## POC:
```
a) Canary Deployment
b) Service Discovery
c) Istio Ingress Gateway

```
## Prerequisite

```
a) Kubernetes Cluster (tested on v1.18.12-gke.1210)
b) Istio Instalation (tested on istio-1.9.0)
C) Kiali Dashboard

```
```
git clone https://github.com/mosesalphonse/quarkus-ref-implementation.git

cd quarkus-ref-implementation

```
##  Build Workloads:

### country-ext-rest-client:
```
	cd workloads/country-ext-rest-client

	mvn clean package -Dmaven.test.skip=true -Dquarkus.container-image.push=true
  
```
### country-sql-client:
```
	cd workloads/country-sql-client

	mvn clean package -Dmaven.test.skip=true -Dquarkus.container-image.push=true
  
```
### frontend:
```
	cd workloads/frontend
	
	mvn clean package -Dmaven.test.skip=true -Dquarkus.container-image.push=true
```
## Deployments:

### Postgress:
```
	kubectl create -f workloads/postgres/yamls/postgres.yaml
```
### Workloads:
```
	kubectl create -f  workloads/country-ext-rest-client/yaml/manifest.yaml	# Deploy Ext Service
	kubectl create -f  workloads/country-sql-client/yaml/manifest.yaml	# Deploy Internal ORM Service
	kubectl create -f  workloads/frontend/yaml/manifest.yaml	# Error Workload, error in logic, it will fail
	kubectl create -f  workloads/frontend/yaml/deployment-v2.yaml	# It is working, but there is no backend call
	kubectl create -f  workloads/frontend/yaml/deployment-v3.yaml	# It is working, with internal and external backend call
```

###  Enable Ingress through ISTIO Ingress gateway to Frontend workload:
	- Use Kiali Console to create Gateway
	- Enable canary deployment using Kiali dashboard

Invoke Frontend App using istio-ingressgateway's public IP

## Testing endpoints:
```
http://{istio-ingress-externalip}/

http://{istio-ingress-externalip}/home?user=india
```
### generare some traffic:
```
for ((i=1;i<=2000;i++)); do   curl -v --header "Connection: keep-alive" "http://ip:80/home?user=uk"; done
```
