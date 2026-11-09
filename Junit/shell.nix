{ pkgs ? import <nixpkgs> {} }:

pkgs.mkShell {
  packages = with pkgs; [
    jdk21
    maven
    glib
    gtk3
    libX11
    libXext
    libXrender
    libXtst
    libXi
    libXxf86vm
    libGL
    alsa-lib
    cups
  ];

  LD_LIBRARY_PATH = pkgs.lib.makeLibraryPath [
    pkgs.glib
    pkgs.gtk3
    pkgs.libX11
    pkgs.libXext
    pkgs.libXrender
    pkgs.libXtst
    pkgs.libXi
    pkgs.libXxf86vm
    pkgs.libGL
    pkgs.alsa-lib
    pkgs.cups
  ];
}
