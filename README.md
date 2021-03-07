# quarkus-ref-implementation

## POC:
```
a) Canary Deployment with A/B Testing
b) Service Discovery
c) Istio Ingress Gateway

```
## Prerequisite

```
a) Kubernetes Cluster (tested on v1.18.12-gke.1210)
b) Istio Instalation (tested on istio-1.9.0)
C) Kiali

```
##  Steps:

```
git clone https://github.com/mosesalphonse/quarkus-ref-implementation.git

cd quarkus-ref-implementation

```
##  Build Workloads:

### country-ext-rest-client (ExtService):
```
	cd workloads/country-ext-rest-client

	mvn clean package -Dmaven.test.skip=true -Dquarkus.container-image.push=true
  
```
### country-sql-client(IntService):
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
### Initial Workloads for A/B testing:
```
	kubectl create -f  workloads/frontend/yaml/deployment-v1.yaml
	kubectl create -f  workloads/frontend/yaml/deployment-v2.yaml
	kubectl create -f  workloads/frontend/yaml/service.yaml
	kubectl create -f  workloads/country-ext-rest-client/yaml/manifest.yaml
	kubectl create -f  workloads/country-sql-client/yaml/manifest.yaml

```
## A/B Testing (50-50 to V1 and V2 of the frontend):


### Deploy new verision - Canary Release:
```
	kubectl create -f  workloads/frontend/yaml/deployment-v3.yaml	# It is working, with internal and external backend call
```

###  Enable Ingress through ISTIO Ingress gateway to Frontend workload:
	- Use Kiali Console to create Request Routing & Gateway
	- Enable canary deployment using Kiali dashboard

Invoke Frontend App using istio-ingressgateway's public IP


## Canary Deployments and A/B Testing:
```
Create Istio Configs:

	kubectl apply -f networks/frontend-vs.yaml	# It is a Frontend Virtual Service with 2 routing rules, one is all traffic which distributes to all 3 									versions of the frontend workloads. The Second rule will identify the specific test user(username=moses) from the 							  header whos traffic alone will be diverted to the newer version(v3) of the workload
	
	kubectl apply -f networks/frontend-dr.yaml	# It is a Destiation Rule for Frontend Service
	
	kubectl apply -f networks/frontend-gw.yaml	# It is a Frontend Gateway which will connect to Istio Ingress Gateway
	
	kubectl apply -f networks/orm-vs.yaml		# Since there is a UI available in this Internal service, created a Virtual Service
	
	kubectl apply -f networks/orm-dr.yaml		# It is a Destiation Rule for Internal service
	
	kubectl apply -f networks/orm-gw.yaml		# It is a Internal Gateway which will connect to Istio Ingress Gateway
	
Note: You can also use Kiali console for enabling the above Istio services.

A/B testing:


curl http://{istio-ingress-externalip}/home?user=india  # traffics will be distributed into v1, v2 and V3 of the frontend workloads equally

curl -H 'username: moses' http://35.232.80.131/home?user=uk  # traffic will always be routed to v3 because we pass username=moses in the header. Using A/B testing business users can able to test the code in LIVE before rolling it out to all the users.

```

## General Testing endpoints:
```
http://{istio-ingress-externalip}/

http://{istio-ingress-externalip}/home?user=india

http://{istio-ingress-externalip}:31400/

http://{istio-ingress-externalip}:31400/countries

```
### generare some traffic:
```
for ((i=1;i<=2000;i++)); do   curl -v --header "Connection: keep-alive" "http://ip:80/home?user=uk"; done
```

### List the namespaces labeld for istio Sidecar injection:
```
	kubectl get namespace -L istio-injection
	
```
