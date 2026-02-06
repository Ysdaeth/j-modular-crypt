# j-crypt-credentials

**j-crypt-credentials** is a Java library that standardizes cryptographic algorithm outputs into  **MCF (Modular Crypt Format)**.  
It provides a complete set of tools for `serialization`, `parsing` and `conversion` of MCF structures.

The library is designed to be used as a reusable dependency.

---

## Features

- Standardized output format based on **Modular Crypt Format (MCF)**
- API for cryptographic algorithm implementations
- Reflection based MCF serialization and parsing
- Configurable type conversion
- Annotation driven object mapping
- Default implementations

---

## Use Cases

- Standardizing encryption outputs
- Interoperable cryptographic storage formats
- Libraries or services requiring MCF compatibility

## Modules & Packages

### API module
The `api` package contains **interfaces intended for third-party applications**.

`implementation` module provide **implementations** of the API that return results in MCF format.

---

## Common module

The `common` module contains core functionality shared across the library.

- Converter
- Parser
- Annotations
- Serializer

---

## Implementation module
Module contains implementations of `API` module, and provides cryptographic algorithms that follow Modular Crypt Format output.

---

## Details
For more details see packages descriptions  
https://github.com/Ysdaeth?tab=packages&repo_name=j-modular-crypt