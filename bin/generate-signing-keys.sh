#!/usr/bin/env bash

## From https://plugins.jetbrains.com/docs/intellij/plugin-signing.html?from=IJPluginTemplate#generate-private-key

# To generate an RSA private.pem private key, run the openssl genpkey command in the terminal, as below:

openssl genpkey \
  -aes-256-cbc \
  -algorithm RSA \
  -out ~/.ssh/jetbrains_private_encrypted.pem \
  -pkeyopt rsa_keygen_bits:4096

# After that, it's required to convert it into RSA form with:

openssl rsa\
  -in ~/.ssh/jetbrains_private_encrypted.pem\
  -out ~/.ssh/jetbrains_private.pem


# As a next step, we'll generate a ~/.ssh/jetbrains_chain.crt certificate chain with:

openssl req\
  -key ~/.ssh/jetbrains_private.pem\
  -new\
  -x509\
  -days 365\
  -out ~/.ssh/jetbrains_chain.crt

echo cat ~/.ssh/jetbrains_private.pem | pbcopy
echo cat ~/.ssh/jetbrains_chain.crt | pbcopy
