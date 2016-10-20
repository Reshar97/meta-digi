# Digi Embedded Yocto (DEY) 2.0
## Release 2.0-r4

This document provides information about Digi Embedded Yocto,
Digi International's professional embedded Yocto development environment.

Digi Embedded Yocto 2.0 is based on the 2.0 (Jethro) Yocto release.

For a full list of supported features and interfaces please refer to the
online documentation.

# Supported Platforms

The current release supports the following hardware platforms:

* Digi ConnectCore 6
  * [Digi P/N CC-WMX-J97C-TN](http://www.digi.com/products/models/cc-wmx-j97c-tn)
  * [Digi P/N CC-WMX-L96C-TE](http://www.digi.com/products/models/cc-wmx-l96c-te)
  * [Digi P/N CC-WMX-L87C-TE](http://www.digi.com/products/models/cc-wmx-l87c-te)
  * [Digi P/N CC-MX-L76C-Z1](http://www.digi.com/products/models/cc-mx-l76c-z1)
  * [Digi P/N CC-MX-L86C-Z1](http://www.digi.com/products/models/cc-mx-l86c-z1)
  * [Digi P/N CC-MX-L96C-Z1](http://www.digi.com/products/models/cc-mx-l96c-z1)
  * [Digi P/N CC-WMX-L76C-TE](http://www.digi.com/products/models/cc-wmx-l76c-te)
  * Digi P/N CC-WMX-K87C-FJA
  * Digi P/N CC-WMX-K77C-TE
  * Digi P/N CC-WMX-L97D-TN
  * Digi P/N CC-WMX-J98C-FJA
  * Digi P/N CC-WMX-J98C-FJA-1

Software for the following hardware platforms is in beta support:

* Digi ConnectCore 6UL
  * [Digi P/N CC-WMX-JN58-NE](http://www.digi.com/products/models/cc-wmx-jn58-ne)
* Digi ConnectCore 6UL Starter Kit
  * [Digi P/N CC-WMX6UL-START](http://www.digi.com/products/models/cc-wmx6ul-start) ([Get Started](https://www.digi.com/resources/documentation/digidocs/90001514/default.htm#concept/yocto/c_get_started_with_yocto.htm))

* Digi ConnectCore 6 Development Kit
  * [Digi P/N CC-WMX6-KIT](http://www.digi.com/products/models/cc-wmx6-kit) ([Get Started](http://www.digi.com/resources/documentation/digidocs/90001945-13/default.htm#concept/yocto/c_get_started_with_yocto.htm%3FTocPath%3DDigi%2520Embedded%2520Yocto%7CGet%2520started%7C_____0))

* Digi ConnectCore 6 SBC
  * [Digi P/N CC-SB-WMX-J97C-1](http://www.digi.com/products/models/cc-sb-wmx-j97c-1)
  * Digi P/N CC-SB-WMX-L87C-1
  * Digi P/N CC-SB-WMX-L76C-1

Previous versions of Digi Embedded Yocto include support for additional Digi
hardware.

# Documentation

Documentation is available online on the Digi documentation site:

* [Digi ConnectCore 6UL](http://www.digi.com/resources/documentation/Digidocs/90001514/default.htm)
* [Digi ConnectCore 6](http://www.digi.com/resources/documentation/Digidocs/90001945-13/default.htm)

# Downloads

* Demo images: ftp://ftp1.digi.com/support/digiembeddedyocto/2.0/r3/images/
* Software Development Kit (SDK): ftp://ftp1.digi.com/support/digiembeddedyocto/2.0/r3/sdk/

# Release Changelog

## 2.0-r4

* TBC

## 2.0-r3

* Support for the new Digi ConnectCore 6UL System-On-Module and Starter Kit
  * U-Boot 2015.04
  * Linux kernel v4.1.28
* Support for TrustFence security enhancements
  * Secure console
  * Secure boot
  * Secure JTAG
  * Encrypted root filesystem
  * True Random Number Generator

## 2.0-r2

* Release based on Yocto 2.0 (Jethro) for ConnectCore 6 SBC including:
  * Update support for new PMIC hardware revision
  * Minor bug fixes

## 2.0-r1

* U-Boot 2015.04 for the Digi ConnectCore 6 System-On-Module and SBC
* Linux kernel v3.14.57 for the Digi ConnectCore 6 System-On-Module and SBC
* Release based on Yocto 2.0 (Jethro) for Digi ConnectCore 6 SBC including:
  * Bluez5
  * QT5 (over X11 and Framebuffer graphical systems)
  * New toolchain based on GCC-5.2 and GLIBC-2.22
  * Cellular support
  * Package upgrades and security fixes

# Known Issues and Limitations

This is a list of known issues and limitations at the time of release. An
updated list can be found on the online documentation.

## Digi ConnectCore 6UL

* In the Bluetooth interface, the UART hardware flow control doesn’t work
properly. To work around this problem the UART is configured without hardware
flow control at 115200 bps, reducing the maximum throughput of this interface.
This problem will be corrected in newer revisions of the hardware.

## Digi ConnectCore 6

* NXP i.MX6 processor has a documented errata (ERR004512) whereby the maximum
performance of the Gigabit FEC is limited to 400Mbps (total for Tx and Rx)
* When using softAP mode on Band A on the Qualcomm AR6233, channels used for
Dynamic Frequency Selection (DFS) are not supported
* The Qualcomm AR6233 firmware does not support the following configuration
modes:
  * Concurrent modes involving P2P mode, such as P2P + softAP or P2P + STA
  * Bluetooth + softAP + STA concurrent mode
* A maximum of five clients are supported when using Qualcomm's AR6233 in
softAP mode
* A maximum of ten connected devices are supported when using Qualcomm's AR6233
Bluetooth Low Energy mode
* NXP i.MX6 processor does not set the sticky bit which write protects the
SRK_REVOKE eFuse on closed devices. This means that in the ConnectCore 6,
key revocation is always possible, no matter the value of the Yocto macro
TRUSTFENCE_UNLOCK_KEY_REVOCATION.
* When using TrustFence (TM)  encrypted images secure boot support, the CAAM
will hang the processor when trying to authenticate an encrypted kernel image
after a failed attempt. Hence the target needs to be reset after an
authentication failure.

## Digi ConnectCore 6 SBC

* The Micrel PHY KSZ9031 may take between five and six seconds to
auto-negotiate with Gigabit switches
* If TrustFence (TM) image encryption support is enabled, the uSD image will
boot a signed U-Boot only.

# Support Contact Information

For support questions please contact Digi Technical Support:

* [Enterprise Support](https://mydigi.secure.force.com/customers/)
* [Product Technical Support](http://www.digi.com/support/product-support)
* [Support forum](http://www.digi.com/support/forum/)
