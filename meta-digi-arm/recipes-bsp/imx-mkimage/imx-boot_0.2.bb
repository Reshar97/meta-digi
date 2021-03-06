# Copyright 2017-2018 NXP

DESCRIPTION = "Generate Boot Loader for i.MX8 device"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"
SECTION = "BSP"

require imx-mkimage_git.inc

inherit deploy

# Add CFLAGS with native INCDIR & LIBDIR for imx-mkimage build
CFLAGS = "-O2 -Wall -std=c99 -static -I ${STAGING_INCDIR_NATIVE} -L ${STAGING_LIBDIR_NATIVE}"

BOOT_TOOLS = "imx-boot-tools"
BOOT_NAME = "imx-boot"
PROVIDES = "${BOOT_NAME}"

IMX_FIRMWARE       = "imx-sc-firmware"
IMX_FIRMWARE_mx8mq = "firmware-imx"
IMX_FIRMWARE_mx8qxp = "firmware-imx digi-sc-firmware"
DEPENDS += " \
    u-boot \
    ${IMX_FIRMWARE} \
    imx-atf \
    ${@bb.utils.contains('COMBINED_FEATURES', 'optee', 'optee-os-imx', '', d)} \
"
DEPENDS_append_mx8mq = " dtc-native"
DEPENDS_append_ccimx8x = " coreutils-native"

# For i.MX 8, this package aggregates the imx-m4-demos
# output. Note that this aggregation replaces the aggregation
# that would otherwise be done in the image build as controlled
# by IMAGE_BOOTFILES_DEPENDS and IMAGE_BOOTFILES in image_types_fsl.bbclass
IMX_M4_DEMOS        = ""
IMX_M4_DEMOS_mx8qm  = "imx-m4-demos"
IMX_M4_DEMOS_mx8qxp = "imx-m4-demos"

# This package aggregates output deployed by other packages,
# so set the appropriate dependencies
do_compile[depends] += " \
    virtual/bootloader:do_deploy \
    ${@' '.join('%s:do_deploy' % r for r in '${IMX_FIRMWARE}'.split() )} \
    imx-atf:do_deploy \
    ${@' '.join('%s:do_deploy' % r for r in '${IMX_M4_DEMOS}'.split() )} \
    ${@bb.utils.contains('COMBINED_FEATURES', 'optee', 'optee-os-imx:do_deploy', '', d)} \
"

# This package aggregates dependencies with other packages,
# so also define the license dependencies.
do_populate_lic[depends] += " \
    virtual/bootloader:do_populate_lic \
    ${@' '.join('%s:do_populate_lic' % r for r in '${IMX_FIRMWARE}'.split() )} \
    imx-atf:do_populate_lic \
    ${@' '.join('%s:do_populate_lic' % r for r in '${IMX_M4_DEMOS}'.split() )} \
    ${@bb.utils.contains('COMBINED_FEATURES', 'optee', 'optee-os-imx:do_populate_lic', '', d)} \
"

SC_FIRMWARE_NAME ?= "scfw_tcm.bin"

ATF_MACHINE_NAME ?= "bl31-imx8qm.bin"
ATF_MACHINE_NAME_mx8qm = "bl31-imx8qm.bin"
ATF_MACHINE_NAME_mx8qxp = "bl31-imx8qxp.bin"
ATF_MACHINE_NAME_mx8mq = "bl31-imx8mq.bin"

DCD_NAME ?= "imx8qm_dcd.cfg.tmp"
DCD_NAME_mx8qm = "imx8qm_dcd.cfg.tmp"
DCD_NAME_mx8qxp = "imx8qx_dcd.cfg.tmp"

UBOOT_NAME = "u-boot-${MACHINE}.bin"
BOOT_CONFIG_MACHINE = "${BOOT_NAME}"

TOOLS_NAME ?= "mkimage_imx8"

SOC_TARGET ?= "iMX8QM"
SOC_TARGET_mx8qm  = "iMX8QM"
SOC_TARGET_mx8qxp = "iMX8QX"
SOC_TARGET_mx8mq  = "iMX8M"

DEPLOY_OPTEE = "false"
DEPLOY_OPTEE_mx8mq = "${@bb.utils.contains('COMBINED_FEATURES', 'optee', 'true', 'false', d)}"

IMXBOOT_TARGETS ?= "${@bb.utils.contains('UBOOT_CONFIG', 'fspi', 'flash_flexspi', \
                       bb.utils.contains('UBOOT_CONFIG', 'nand', 'flash_nand', \
                                                                 'flash_multi_cores flash flash_dcd', d), d)}"
IMXBOOT_TARGETS_mx8qxp = "${@bb.utils.contains('UBOOT_CONFIG', 'fspi', 'flash_flexspi', \
                       bb.utils.contains('UBOOT_CONFIG', 'nand', 'flash_nand', \
                                                                 'flash flash_all', d), d)}"
IMXBOOT_TARGETS_imx8qxpddr3arm2 = "flash_ddr3_dcd_a0"

S = "${WORKDIR}/git"

do_compile () {
    if [ "${SOC_TARGET}" = "iMX8M" ]; then
        echo 8MQ boot binary build
        for ddr_firmware in ${DDR_FIRMWARE_NAME}; do
            echo "Copy ddr_firmware: ${ddr_firmware} from ${DEPLOY_DIR_IMAGE} -> ${S}/${SOC_TARGET} "
            cp ${DEPLOY_DIR_IMAGE}/${ddr_firmware}               ${S}/${SOC_TARGET}/
        done
        cp ${DEPLOY_DIR_IMAGE}/signed_hdmi_imx8m.bin             ${S}/${SOC_TARGET}/
        cp ${DEPLOY_DIR_IMAGE}/u-boot-spl.bin-${MACHINE}-${UBOOT_CONFIG} ${S}/${SOC_TARGET}/u-boot-spl.bin
        cp ${DEPLOY_DIR_IMAGE}/${BOOT_TOOLS}/${UBOOT_DTB_NAME}   ${S}/${SOC_TARGET}/
        cp ${DEPLOY_DIR_IMAGE}/${BOOT_TOOLS}/u-boot-nodtb.bin    ${S}/${SOC_TARGET}/
        cp ${DEPLOY_DIR_IMAGE}/${BOOT_TOOLS}/mkimage_uboot       ${S}/${SOC_TARGET}/

        cp ${DEPLOY_DIR_IMAGE}/${BOOT_TOOLS}/${ATF_MACHINE_NAME} ${S}/${SOC_TARGET}/bl31.bin
        cp ${DEPLOY_DIR_IMAGE}/${UBOOT_NAME}                     ${S}/${SOC_TARGET}/u-boot.bin

    elif [ "${SOC_TARGET}" = "iMX8QM" ]; then
        echo 8QM boot binary build
        cp ${DEPLOY_DIR_IMAGE}/${BOOT_TOOLS}/${SC_FIRMWARE_NAME} ${S}/${SOC_TARGET}/scfw_tcm.bin
        cp ${DEPLOY_DIR_IMAGE}/${BOOT_TOOLS}/${ATF_MACHINE_NAME} ${S}/${SOC_TARGET}/bl31.bin
        cp ${DEPLOY_DIR_IMAGE}/${UBOOT_NAME}                     ${S}/${SOC_TARGET}/u-boot.bin

        cp ${DEPLOY_DIR_IMAGE}/imx8qm_m4_0_TCM_rpmsg_lite_pingpong_rtos_linux_remote.bin ${S}/${SOC_TARGET}/m40_tcm.bin
        cp ${DEPLOY_DIR_IMAGE}/imx8qm_m4_1_TCM_rpmsg_lite_pingpong_rtos_linux_remote.bin ${S}/${SOC_TARGET}/m41_tcm.bin

    else
        echo 8QX boot binary build
        cp ${DEPLOY_DIR_IMAGE}/imx8qx_m4_TCM_rpmsg_lite_pingpong_rtos_linux_remote.bin ${S}/${SOC_TARGET}/m40_tcm.bin
        cp ${DEPLOY_DIR_IMAGE}/imx8qx_m4_TCM_rpmsg_lite_pingpong_rtos_linux_remote.bin ${S}/${SOC_TARGET}/CM4.bin
        cp ${DEPLOY_DIR_IMAGE}/ahab-container.img ${S}/${SOC_TARGET}/
        cp ${DEPLOY_DIR_IMAGE}/${BOOT_TOOLS}/${ATF_MACHINE_NAME} ${S}/${SOC_TARGET}/bl31.bin
        for type in ${UBOOT_CONFIG}; do
            RAM_SIZE="$(echo ${type} | sed -e 's,.*\([0-9]\+GB\),\1,g')"
            cp ${DEPLOY_DIR_IMAGE}/${BOOT_TOOLS}/${UBOOT_NAME}-${type}         ${S}/${SOC_TARGET}/u-boot.bin-${type}
            cp ${DEPLOY_DIR_IMAGE}/${BOOT_TOOLS}/${SC_FIRMWARE_NAME}-${RAM_SIZE} ${S}/${SOC_TARGET}/scfw_tcm.bin-${RAM_SIZE}
        done
    fi

    # Copy TEE binary to SoC target folder to mkimage
    if ${DEPLOY_OPTEE}; then
        cp ${DEPLOY_DIR_IMAGE}/tee.bin             ${S}/${SOC_TARGET}/
    fi

    # mkimage for i.MX8
    for type in ${UBOOT_CONFIG}; do
        cd ${S}/${SOC_TARGET}
        ln -sf u-boot.bin-${type} u-boot.bin
        RAM_SIZE="$(echo ${type} | sed -e 's,.*\([0-9]\+GB\),\1,g')"
        ln -sf scfw_tcm.bin-${RAM_SIZE} scfw_tcm.bin
        cd -
        for target in ${IMXBOOT_TARGETS}; do
            echo "building ${SOC_TARGET} - ${type} - ${target}"
            make SOC=${SOC_TARGET} ${target}
            if [ -e "${S}/${SOC_TARGET}/flash.bin" ]; then
                cp ${S}/${SOC_TARGET}/flash.bin ${S}/${BOOT_CONFIG_MACHINE}-${type}.bin-${target}
            fi
        done
        rm ${S}/${SOC_TARGET}/scfw_tcm.bin
        rm ${S}/${SOC_TARGET}/u-boot.bin
        # Remove u-boot-atf.bin so it gets generated with the next iteration's U-Boot
        rm ${S}/${SOC_TARGET}/u-boot-atf.bin
    done
}

SYSROOT_DIRS += "/boot"

do_install () {
    install -d ${D}/boot
    for type in ${UBOOT_CONFIG}; do
        for target in ${IMXBOOT_TARGETS}; do
            install -m 0644 ${S}/${BOOT_CONFIG_MACHINE}-${type}.bin-${target} ${D}/boot/
        done
    done
}

DEPLOYDIR_IMXBOOT = "${BOOT_TOOLS}"
do_deploy () {
    install -d ${DEPLOYDIR}/${DEPLOYDIR_IMXBOOT}

    # copy the tool mkimage to deploy path along with sc fw and dcd
    if [ "${SOC_TARGET}" = "iMX8M" ]; then
        install -m 0644 ${DEPLOY_DIR_IMAGE}/u-boot-spl.bin-${MACHINE}-${UBOOT_CONFIG} ${DEPLOYDIR}/${DEPLOYDIR_IMXBOOT}
        for ddr_firmware in ${DDR_FIRMWARE_NAME}; do
            install -m 0644 ${DEPLOY_DIR_IMAGE}/${ddr_firmware} ${DEPLOYDIR}/${DEPLOYDIR_IMXBOOT}
        done
        install -m 0644 ${DEPLOY_DIR_IMAGE}/signed_hdmi*.bin ${DEPLOYDIR}/${DEPLOYDIR_IMXBOOT}

        install -m 0755 ${S}/${SOC_TARGET}/${TOOLS_NAME} ${DEPLOYDIR}/${DEPLOYDIR_IMXBOOT}

        install -m 0755 ${S}/${SOC_TARGET}/mkimage_fit_atf.sh ${DEPLOYDIR}/${DEPLOYDIR_IMXBOOT}
    elif [ "${SOC_TARGET}" = "iMX8QM" ]; then
        install -m 0644 ${S}/${SOC_TARGET}/${DCD_NAME} ${DEPLOYDIR}/${DEPLOYDIR_IMXBOOT}

        install -m 0755 ${S}/${TOOLS_NAME} ${DEPLOYDIR}/${BOOT_TOOLS}
    else
        install -m 0644 ${S}/${SOC_TARGET}/ahab-container.img ${DEPLOYDIR}/${DEPLOYDIR_IMXBOOT}
        install -m 0644 ${S}/${SOC_TARGET}/m40_tcm.bin ${DEPLOYDIR}/${DEPLOYDIR_IMXBOOT}
        install -m 0644 ${S}/${SOC_TARGET}/CM4.bin ${DEPLOYDIR}/${DEPLOYDIR_IMXBOOT}

        install -m 0755 ${S}/${TOOLS_NAME} ${DEPLOYDIR}/${BOOT_TOOLS}
    fi

    # copy tee.bin to deploy path
    if "${DEPLOY_OPTEE}"; then
        install -m 0644 ${DEPLOY_DIR_IMAGE}/tee.bin ${DEPLOYDIR}/${DEPLOYDIR_IMXBOOT}
    fi

    # copy makefile (soc.mak) for reference
    install -m 0644 ${S}/${SOC_TARGET}/soc.mak     ${DEPLOYDIR}/${DEPLOYDIR_IMXBOOT}

    # copy the generated boot image to deploy path
    for type in ${UBOOT_CONFIG}; do
        IMAGE_IMXBOOT_TARGET=""
        for target in ${IMXBOOT_TARGETS}; do
            # Use first "target" as IMAGE_IMXBOOT_TARGET
            if [ "$IMAGE_IMXBOOT_TARGET" = "" ]; then
                IMAGE_IMXBOOT_TARGET="$target"
                echo "Set boot target as $IMAGE_IMXBOOT_TARGET"
            fi
            install -m 0644 ${S}/${BOOT_CONFIG_MACHINE}-${type}.bin-${target} ${DEPLOYDIR}
        done
        cd ${DEPLOYDIR}
        ln -sf ${BOOT_CONFIG_MACHINE}-${type}.bin-${IMAGE_IMXBOOT_TARGET} ${BOOT_CONFIG_MACHINE}-${type}.bin
        ln -sf ${BOOT_CONFIG_MACHINE}-${type}.bin-${IMAGE_IMXBOOT_TARGET} ${BOOT_CONFIG_MACHINE}-${MACHINE}.bin
        cd -
    done
}

addtask deploy before do_build after do_compile

FILES_${PN} = "/boot"

COMPATIBLE_MACHINE = "(mx8qm|mx8qxp|mx8mq)"
PACKAGE_ARCH = "${MACHINE_ARCH}"
