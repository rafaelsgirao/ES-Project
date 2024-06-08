{
  inputs = {
    nixpkgs.url = "flake:nixpkgs";
    flake-parts = {
      url = "github:hercules-ci/flake-parts";
      inputs.nixpkgs-lib.follows = "nixpkgs";
    };
    pre-commit-hooks-nix = {
      url = "github:cachix/pre-commit-hooks.nix";
      inputs.nixpkgs.follows = "nixpkgs";
      inputs.nixpkgs-stable.follows = "nixpkgs";
    };
    treefmt-nix = {
      url = "github:numtide/treefmt-nix";
      inputs.nixpkgs.follows = "nixpkgs";
    };
    devenv.url = "github:cachix/devenv";
    nixos-config.url = "github:rafaelsgirao/nixos-config";
  };
  outputs = inputs@{ flake-parts, ... }:
    flake-parts.lib.mkFlake { inherit inputs; } {
      imports = [
        # To import a flake module
        # 1. Add foo to inputs
        # 2. Add foo as a parameter to the outputs function
        # 3. Add here: foo.flakeModule
        inputs.pre-commit-hooks-nix.flakeModule
        inputs.devenv.flakeModule
        inputs.treefmt-nix.flakeModule

      ];
      flake = {
        # Put your original flake attributes here.
      };
      #systems = [ "x86_64-linux" "aarch64-linux" "aarch64-darwin" "x86_64-darwin" ];
      systems = [
        # systems for which you want to build the `perSystem` attributes
        "x86_64-linux"
        "aarch64-linux"
        "x86_64-darwin"
      ];
      # perSystem = { config, self', inputs', pkgs, system, ... }: {
      perSystem = { config, pkgs, inputs', ... }: {
        # Per-system attributes can be defined here. The self' and inputs'
        # module parameters provide easy access to attributes of the same
        # system.
        devenv.shells.default = {
          # https://devenv.sh/reference/options/
          packages = with pkgs; [
            commitizen
            cypress
          ];

          enterShell = ''
            ${config.pre-commit.installationScript}
            export POSTGRES_DB=hedb
            export POSTGRES_USER=postgres
            export POSTGRES_PASSWORD=postgres
            export POSTGRES_HOST_AUTH_METHOD=trust
            export PSQL_INT_TEST_DB_USERNAME=postgres
            export PSQL_INT_TEST_DB_PASSWORD=postgres
            export cypress_psql_db_name=hedb
            export cypress_psql_db_username=postgres
            export cypress_psql_db_password=postgres
            export cypress_psql_db_host=localhost
            export cypress_psql_db_port=5432
          '';
          services.postgres = {
            enable = true;
            initialDatabases = [{ name = "hedb"; }];
            initialScript = ''
              CREATE USER postgres WITH SUPERUSER LOGIN PASSWORD 'postgres';
            '';
            listen_addresses =
              "127.0.0.1,::1"
            ;
          };
          languages.java = {
            enable = true;
            jdk.package = pkgs.openjdk17-bootstrap;
            maven.enable = true; #not sure if needed
            # gradle.enable = true; #not sure if needed
          };
          languages.javascript.enable = true;
          #     devcontainer.enable = true;
          # https://devenv.sh/services/
        };
        pre-commit = {
          check.enable = true;
          settings.settings = {
            deadnix.edit = true;
          };
          settings.hooks = {
            actionlint.enable = true;
            # denolint.enable = true;
            deadnix.enable = true;
            statix.enable = true;
            markdownlint.enable = true;
            treefmt.enable = true;
            # prettier.enable = true;
            commitizen = {
              enable = true;
              description = "Check whether the current commit message follows commiting rules. Allow empty commit messages by default, because they typically indicate to Git that the commit should be aborted.";
              entry = "${pkgs.commitizen}/bin/cz check --allow-abort --commit-msg-file";
              stages = [ "commit-msg" ];

              # pass_filenames = false;
            };
            check-git-email = {
              enable = true;
              description = "WIP";
              entry = "${inputs'.nixos-config.packages.pre-commit-macadmin}/bin/check-git-config-email --domains tecnico.ulisboa.pt";
              # stages = [ "commit-msg" ];

              # pass_filenames = false;
            };
          };
        };
        treefmt.projectRootFile = ./flake.nix;
        treefmt.programs = {
          nixpkgs-fmt.enable = true;
          yamlfmt.enable = true;
          shfmt.enable = true;
          mdformat.enable = true;
          google-java-format.enable = true;
          prettier.enable = true;
        };
      };
    };
}

