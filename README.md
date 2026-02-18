# j-modular-crypt

## About
Annotation driven Java library that makes Modular Crypt Format (MCF) outputs easier to create.
Core functionality of the library is serialization and deserialization MCF structures, parsing,
and basic data types conversion. 

---

## Features

- Standardized output format based on **Modular Crypt Format (MCF)**
- API for cryptographic algorithm implementations
- Reflection based MCF serialization and deserializarion
- Configurable type conversion
- Annotation driven design
- Default implementations

---

## Use Cases

- Standardization encryption and hashing outputs
- Interoperable cryptographic storage formats

## Modules & Packages

### API module
The `api` module contains interfaces intended for third-party applications.

---

## Core module

The `core` module contains core functionality of the library.

- Converter
- Parser
- Annotations
- Serializer

---

## Implementation module
Implementation module contains the base implementations of the `API` module.

Provides Modular Crypt Format algorithm outputs. Cryptographic algorithms are supplied by the Java Security Provider. This is not core feature of the library, it serves as an access layer to cryptographic algorithms to get started quickly.

### Mac

- HMac Sha256
- HMac Sha384
- HMac Sha512

### Encryptor

- RSA OAEP
- RSA OAEP + AES GCM
- AES GCM

---

## Requirements
- Java 17+
- Java security provider for algorithms specified above.


## Details
For more details see packages descriptions  
https://github.com/Ysdaeth?tab=packages&repo_name=j-modular-crypt
