#!/bin/bash

echo "Before running this script you must:"
echo "   1) Create a new repository to serve as your Abra-Collaboratory framework based repository"
echo "   2) Create the first wiki page in your repository manually."

if [ $# != 2 ]
  then
    echo "Please provide user name and project name"
    exit 1
fi

read -p "Are you ready to continue (y/n)? " -n 1 -r
echo    # (optional) move to a new line
if [[ ! $REPLY =~ ^[Yy]$ ]]
then
    [[ "$0" = "$BASH_SOURCE" ]] && exit 1 || return 1 # handle exits from shell or function but don't exit interactive shell
fi

USER_NAME=$1
PROJECT_NAME=$2

git clone https://github.com/callahantiff/Abra-Collaboratory.git
mkdir resources
mkdir .github
mv Abra-Collaboratory/.github/ISSUE_TEMPLATE .github/
mv Abra-Collaboratory/resources/documentation resources/
mv Abra-Collaboratory/resources/New_Collaborators_Invitation_Email.md resources/

rm -rf Abra-Collaboratory/

git add .
git commit -m "Added Abra-C framework files"

find . -type f ! -path '*/\.git/*' -exec sed -i 's/callahantiff/{$USER_NAME}/g' {} \;
find . -type f ! -path '*/\.git/*' -exec sed -i 's/Abra-Collaboratory/{$PROJECT_NAME}/g' {} \;

git add .
git commit -m "Updated links and assignees"
git push

git clone https://github.com/callahantiff/Abra-Collaboratory.wiki.git

git clone https://github.com/${USER_NAME}/${PROJECT_NAME}.wiki.git
mv Abra-Collaboratory.wiki/Our-Reproducible-Repository-Guidelines-😃.md ${USER_NAME}/${PROJECT_NAME}.wiki
mv Abra-Collaboratory.wiki/Using-GitHub-as-a-Reproducible-Research-Platform.md ${USER_NAME}/${PROJECT_NAME}.wiki 
cd ${USER_NAME}/${PROJECT_NAME}.wiki
git add .
git commit -m "Added Abra-C guidelines"

# git remote add wiki-fork https://github.com/${USER_NAME}/${PROJECT_NAME}.wiki.git
git push wiki-fork -f

cd ..
rm -rf Abra-Collaboratory.wiki
rm -rf ${USER_NAME}/${PROJECT_NAME}.wiki

echo "The next step is to copy the project board templates."
echo "See here for instructions: https://github.com/callahantiff/Abra-Collaboratory#add-projects-boards"
