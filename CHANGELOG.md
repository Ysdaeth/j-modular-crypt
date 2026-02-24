# Changelog


## API

## Version: 1.3.0
- Encryptor API now requires `Key` for cryptographic operations

Reason: Simplify data encryption and decryption, while using dynamic key rotation, for 
a multithreaded environments with Encryptor as a single bean.

### Version: 1.2.0
- Decreased required java version to java 17

Reason: Support for older Java versions.


### Version 1.1.0
- New `Mac` interface
- Removed symmetric, hybrid and asymmetric encryptor interface.

Reason: To make Encryptors easier to manage, provide hashing functions.



## IMPLEMENTATION

## Version: 1.3.0
- Implementations of the Encryptor interface now requires a `Key` for cryptographic operations.
- Encryptors no longer store a `Key`.

Reason: To reduce responsibility related to the key caching, and simplify data encryption and decryption,
while using dynamic key rotation, for a multithreaded environments with Encryptor as a single bean.

** Minor fixes **
- Encryptors will throw runtime exception on wrong algorithm identifier when decrypting
- Encryptor AES now will throw Key Exception on invalid key 


## Version: 1.2.0
- Decreased required java version to java 17

Reason: To make Encryptors easier to manage, provide hashing functions.


### Version 1.1.0
- New HMac instances (from java security provider)
- Removed symmetric, hybrid and asymmetric encryptors now are implementation of the Encryptor interface 
Reason: Api change, new api interface



## CORE

### Version: 1.2.0
- Unified basic converter, added new types
- Added serializer factory
- Change module name `common` -> `core`
- Decreased required java version to java 17

Reason: Easier grouping serializers and reduced configuration boilerplate. 
Since this repository core feature is to make Modular Crypt Format output not encryption itself, 
module name was changed from `common` to `core` and prepare to be separate dependency.

**Bug fixes**

- Method handle setter for class `record` now will properly create ClassSerializer when IllegalAccessException occurs.

### Version 1.1.0
- Changed conversion class to flexible Conversion Registry

Reason: More flexible types conversion for basic MFC fields, and easier registering new converting functions.
