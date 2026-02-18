# j-modular-crypt

## About
Annotation driven Java library that make Modular Crypt Format (MCF) outputs easier to create.
Core functionality of the library is serialization and deserialization MCF structures, parsing,
and conversion of basic data types commonly used used by MCF. 

---

## Features

- Standardized output format based on **Modular Crypt Format (MCF)**
- API for cryptographic algorithm implementations
- Reflection based MCF serialization and parsing
- Configurable type conversion
- Annotation driven design
- Default implementations

---

## Use Cases

- Standardizing encryption outputs
- Interoperable cryptographic storage formats

## Modules & Packages

### API module
The `api` package contains interfaces intended for third-party applications.

---

## Common module

The `core` module contains core functionality shared across the library.

- Converter
- Parser
- Annotations
- Serializer

---

## Implementation module
This module contains the base implementations of the `API` module/package.

Provides encrypted/hashed data where algorithm output matches Modular Crypt Format. Cryptographic algorithms are supplied by the Java Security Provider. This is not core feature of the library, it serves as an access layer to cryptographic algorithms to get started quickly.

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