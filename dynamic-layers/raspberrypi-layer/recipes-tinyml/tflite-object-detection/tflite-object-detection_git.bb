HOMEPAGE = "https://github.com/tensorflow/examples/tree/master/lite/examples/object_detection/raspberry_pi"
DESCRIPTION = "TensorFlow Lite Python object detection example with Raspberry Pi"
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

SRCREV = "a9265a997d0410c431a292e3553646b3b655ea1f"
SRC_URI += " \
    git://github.com/tensorflow/examples;branch=master \
    https://tfhub.dev/tensorflow/lite-model/efficientdet/lite0/detection/metadata/1?lite-format=tflite;downloadfilename=efficientdet_lite0.tflite \
    file://detect.sh.in \
"
SRC_URI[sha256sum] = "2e04c53bfeac0ac2a30c057c7e2a777594ce39baaac35a92f74fb1e8c4fc4e0b"

# Redefine unpacked recipe source code location (S) according to the Git fetcher's default checkout location (destsuffix)
# (see https://docs.yoctoproject.org/bitbake/bitbake-user-manual/bitbake-user-manual-fetching.html#git-fetcher-git for details)
S = "${WORKDIR}/git"

FILES_${PN} += " \
    ${bindir}/detect.py \
    ${bindir}/object_detector.py \
    ${bindir}/utils.py \
    ${bindir}/detect \
    ${datadir}/tflite/efficientdet_lite0.tflite \
"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/lite/examples/object_detection/raspberry_pi/detect.py ${D}${bindir}
    install -m 0755 ${S}/lite/examples/object_detection/raspberry_pi/object_detector.py ${D}${bindir}
    install -m 0755 ${S}/lite/examples/object_detection/raspberry_pi/utils.py ${D}${bindir}
    install -m 0755 ${WORKDIR}/detect.sh.in ${D}${bindir}/detect
    sed -i "s@{{model}}@${datadir}/tflite/efficientdet_lite0.tflite@" ${D}${bindir}/detect

    install -d ${D}${datadir}/tflite
    install -m 0644 ${WORKDIR}/efficientdet_lite0.tflite ${D}${datadir}/tflite
}
