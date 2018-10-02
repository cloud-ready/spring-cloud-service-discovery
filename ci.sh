#!/usr/bin/env bash


#echo -e "\n>>>>>>>>>> ---------- override options ---------- >>>>>>>>>>"
#export CI_INFRA_OPT_GIT_PREFIX="https://gitlab.com"
#export CI_OPT_CI_SCRIPT="${CI_INFRA_OPT_GIT_PREFIX}/ci-and-cd/maven-build/raw/develop/src/main/ci-script/lib_ci.sh"
#echo -e "<<<<<<<<<< ---------- override options ---------- <<<<<<<<<<\n"


echo -e "\n>>>>>>>>>> ---------- default options ---------- >>>>>>>>>>"
if [ -z "${CI_INFRA_OPT_GIT_PREFIX}" ]; then CI_INFRA_OPT_GIT_PREFIX="https://gitlab.com"; fi


#if [ -z "${CI_OPT_CI_SCRIPT}" ]; then CI_OPT_CI_SCRIPT="${CI_INFRA_OPT_GIT_PREFIX}/ci-and-cd/maven-build/raw/develop/src/main/ci-script/lib_ci.sh"; fi
if [ -z "${CI_OPT_CI_SCRIPT}" ]; then CI_OPT_CI_SCRIPT="${CI_INFRA_OPT_GIT_PREFIX}/ci-and-cd/maven-build/raw/v0.3.7/src/main/ci-script/lib_ci.sh"; fi
if [ -z "${CI_OPT_DOCKER_IMAGE_PREFIX}" ]; then CI_OPT_DOCKER_IMAGE_PREFIX="cloudready/"; fi
if [ -z "${CI_OPT_GITHUB_SITE_PUBLISH}" ]; then CI_OPT_GITHUB_SITE_PUBLISH="false"; fi
if [ -z "${CI_OPT_GITHUB_SITE_REPO_OWNER}" ]; then CI_OPT_GITHUB_SITE_REPO_OWNER="cloud-ready"; fi
if [ -z "${CI_OPT_GPG_KEYNAME}" ]; then CI_OPT_GPG_KEYNAME="59DBF10E"; fi
if [ -z "${CI_OPT_INFRASTRUCTURE}" ]; then CI_OPT_INFRASTRUCTURE="opensource"; fi
#if [ -z "${CI_OPT_MAVEN_BUILD_REPO}" ]; then CI_OPT_MAVEN_BUILD_REPO="${CI_INFRA_OPT_GIT_PREFIX}/ci-and-cd/maven-build/raw/develop"; fi
if [ -z "${CI_OPT_MAVEN_BUILD_REPO}" ]; then CI_OPT_MAVEN_BUILD_REPO="${CI_INFRA_OPT_GIT_PREFIX}/ci-and-cd/maven-build/raw/v0.3.7"; fi
if [ -z "${CI_OPT_MAVEN_EFFECTIVE_POM}" ]; then CI_OPT_MAVEN_EFFECTIVE_POM="false"; fi
if [ -z "${CI_OPT_ORIGIN_REPO_SLUG}" ]; then if [ -n "${CI_PROJECT_PATH}" ]; then CI_OPT_ORIGIN_REPO_SLUG="gitlab-cloud-ready/spring-cloud-service-discovery"; else CI_OPT_ORIGIN_REPO_SLUG="cloud-ready/spring-cloud-service-discovery"; fi; fi
if [ -z "${CI_OPT_SITE}" ]; then CI_OPT_SITE="true"; fi
if [ -z "${CI_OPT_SITE_PATH_PREFIX}" ] && [ "${CI_OPT_GITHUB_SITE_PUBLISH}" == "true" ]; then
    # github site repo cloud-ready/cloud-ready (CI_OPT_GITHUB_SITE_REPO_NAME)
    CI_OPT_SITE_PATH_PREFIX="cloud-ready"
elif [ -z "${CI_OPT_SITE_PATH_PREFIX}" ] && [ "${CI_OPT_GITHUB_SITE_PUBLISH}" == "false" ]; then
    # site in nexus3 raw repository
    CI_OPT_SITE_PATH_PREFIX="cloud-ready"
fi
if [ -z "${CI_OPT_SONAR_ORGANIZATION}" ]; then CI_OPT_SONAR_ORGANIZATION="home1-oss-github"; fi
if [ -z "${CI_OPT_SONAR}" ]; then CI_OPT_SONAR="true"; fi

if [ -z "${LOGGING_LEVEL_ROOT}" ]; then export LOGGING_LEVEL_ROOT="INFO"; fi
echo -e "<<<<<<<<<< ---------- default options ---------- <<<<<<<<<<\n"


echo -e "\n>>>>>>>>>> ---------- call remote script ---------- >>>>>>>>>>"
echo "set -e; curl -f -s -L ${CI_OPT_CI_SCRIPT} > /tmp/$(basename $(pwd))-lib_ci.sh; set +e; source /tmp/$(basename $(pwd))-lib_ci.sh"
set -e; curl -f -s -L ${CI_OPT_CI_SCRIPT} > /tmp/$(basename $(pwd))-lib_ci.sh; set +e; source /tmp/$(basename $(pwd))-lib_ci.sh
echo -e "<<<<<<<<<< ---------- call remote script ---------- <<<<<<<<<<\n"
