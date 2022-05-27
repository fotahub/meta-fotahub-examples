HOMEPAGE = "https://github.com/tanhouren/Face_mask_detector"
DESCRIPTION = "TensorFlow Lite Python face mask detector with Raspberry Pi"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

COMPATIBLE_MACHINE = "^rpi$"

inherit features_check

# Make sure that opencv gets built with gtk enabled
# (see layers/meta-openembedded/meta-oe/recipes-support/opencv/opencv_4.1.0.bb:105 for details)
REQUIRED_DISTRO_FEATURES = "x11"

RDEPENDS_${PN} += " \
    bash \
    python3-opencv \
    python3-tensorflow-lite \
    python3-tflite-support \
"

SRCREV = "d3b1414a128fef5ced55cac468373e6fab58ccf4"
SRC_URI += " \
    git://github.com/tanhouren/Face_mask_detector;branch=master \
    file://detect.py \
    file://detect.sh.in \
"

# Redefine unpacked recipe source code location (S) according to the Git fetcher's default checkout location (destsuffix)
# (see https://docs.yoctoproject.org/bitbake/bitbake-user-manual/bitbake-user-manual-fetching.html#git-fetcher-git for details)
S = "${WORKDIR}/git"

FILES_${PN} += " \
    ${bindir}/detect.py \
    ${bindir}/detect \
    ${datadir}/tflite/ssd_mobilenet_v2_fpnlite.tflite \
"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/detect.py ${D}${bindir}/detect.py
    install -m 0755 ${WORKDIR}/detect.sh.in ${D}${bindir}/detect
    sed -i "s@{{model}}@${datadir}/tflite/ssd_mobilenet_v2_fpnlite.tflite@" ${D}${bindir}/detect

    install -d ${D}${datadir}/tflite
    install -m 0644 ${S}/ssd_mobilenet_v2_fpnlite.tflite ${D}${datadir}/tflite
}
