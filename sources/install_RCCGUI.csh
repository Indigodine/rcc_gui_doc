#!/bin/tcsh
# install IHMRunControl package
#set echo

if ( $# == 0 ) then
    echo usage : install_RCCGUI.csh install_path
    exit
else
    set INSTALL_PATH = $1
endif

echo installing RCCGUI in $INSTALL_PATH

mkdir -p $INSTALL_PATH

cp IHMRC.jar rcc.wsdl $INSTALL_PATH
