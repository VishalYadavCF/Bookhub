import subprocess
import yaml
from deepdiff import DeepDiff
import os
import sys

def run_command(command):
    result = subprocess.run(command, shell=True, capture_output=True, text=True)
    return result.returncode == 0, result.stdout.strip(), result.stderr.strip()

def get_file_from_branch(branch, file_path):
    cmd = f"git show {branch}:{file_path}"
    success, output, error = run_command(cmd)
    if success:
        return yaml.safe_load(output)
    else:
        print(f"❌ Cannot find {file_path} in branch '{branch}': {error}")
        return None

def check_file_exists_in_branch(branch, file_path):
    code, _, _ = run_command(f"git cat-file -e {branch}:{file_path}")
    return code == 0

def load_local_yaml(file_path):
    try:
        with open(file_path, "r") as f:
            return yaml.safe_load(f)
    except Exception as e:
        print(f"❌ Failed to read local file {file_path}: {e}")
        return None

def download_openapi_yaml(default_url="http://localhost:8088/bookhub/api-docs.yaml", output_file="openapi.yaml"):
    user_url = input(f"🔍 openapi.yaml not found. Enter curl URL to download specs [default: {default_url}]: ").strip()
    final_url = user_url if user_url else default_url
    print(f"📥 Downloading OpenAPI spec from {final_url} ...")
    success, _, error = run_command(f"curl {final_url} -o {output_file}")
    if success:
        print("✅ openapi.yaml downloaded successfully.")
        return load_local_yaml(output_file)
    else:
        print(f"❌ Failed to download openapi.yaml: {error}")
        return None

def extract_endpoints(spec):
    return spec.get("paths", {})

def compare_paths(master_paths, current_paths):
    added, changed = {}, {}

    for path, methods in current_paths.items():
        if path not in master_paths:
            added[path] = methods
        else:
            for method, details in methods.items():
                if method not in master_paths[path]:
                    added.setdefault(path, {})[method] = details
                elif DeepDiff(master_paths[path][method], details, ignore_order=True):
                    changed.setdefault(path, {})[method] = details

    return added, changed

def save_diff_to_yaml(added, changed, output_path="diffopenapi.yaml"):
    diff = {"added": added, "changed": changed}
    with open(output_path, "w") as f:
        yaml.dump(diff, f, sort_keys=False)
    print(f"✅ Diff saved to {output_path}")

def main():
    if len(sys.argv) < 2:
        print("❗Usage: python compare_openapi.py <master_branch> [compare_branch]")
        sys.exit(1)

    master_branch = sys.argv[1]
    compare_branch = sys.argv[2] if len(sys.argv) > 2 else None
    openapi_path = "openapi.yaml"

    # --- Load from master branch ---
    if not check_file_exists_in_branch(master_branch, openapi_path):
        print(f"❌ openapi.yaml not found in branch '{master_branch}' — cannot proceed.")
        sys.exit(1)

    master_spec = get_file_from_branch(master_branch, openapi_path)
    if master_spec is None:
        sys.exit(1)

    # --- Load from compare branch or local ---
    if compare_branch:
        if not check_file_exists_in_branch(compare_branch, openapi_path):
            print(f"⚠️ openapi.yaml not found in branch '{compare_branch}'.")
            compare_spec = download_openapi_yaml()
        else:
            compare_spec = get_file_from_branch(compare_branch, openapi_path)
    else:
        if not os.path.exists(openapi_path):
            compare_spec = download_openapi_yaml()
        else:
            compare_spec = load_local_yaml(openapi_path)

    if compare_spec is None:
        print("❌ Cannot proceed without a valid openapi.yaml to compare.")
        sys.exit(1)

    # --- Compare and Save Diff ---
    added, changed = compare_paths(extract_endpoints(master_spec), extract_endpoints(compare_spec))
    save_diff_to_yaml(added, changed)

if __name__ == "__main__":
    main()
