DESCRIPTION = "Exemplary service Waiting for network to come up before running"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

RDEPENDS_${PN} += "bash"

SRC_URI += " \
    file://network-up-probe.service \
    file://network-up-probe.sh \
"

SYSTEMD_SERVICE_${PN} = "network-up-probe.service"

inherit systemd

do_install () {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/network-up-probe.service ${D}${systemd_system_unitdir}/network-up-probe.service

    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/network-up-probe.sh ${D}${bindir}/network-up-probe.sh
}

FILES_${PN} = " \
                ${systemd_system_unitdir}/network-up-probe.service \
                ${bindir}/network-up-probe.sh \
              "
