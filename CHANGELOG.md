# Changelog

## Version: 1.2.x
- Unified basic converter, added new types
- Added serializer factory
- Change module name `common` -> `core`
- Decreased required java version to java 17

Reason: Easier grouping serializers and reduced configuration boilerplate. Since this repository core feature is to make Modular Crypt Format output not encryption itself, module name was changed from `common` to `core` and prepare to be separate dependency.

**Bug fixes**

- Method handle setter for class `record` now will properly create ClassSerializer when IllegalAccessException occurs.   


## Version 1.1.x
- New `Mac` interface
- New HMac instances (from java security provider)
- Removed symmetric, hybrid and asymmetric encryptor interface 
- Changed conversion class to flexible Conversion Registry

Reason: More flexible types conversion for basic MFC fields, and easier registering new converting functions.