import sys, os
from rdkit import Chem
from FileParser import FileParser

def save_InChi(mol, **kwargs):
    path = kwargs["path"]
    name = mol.GetProp("_Name")
    InChi = Chem.MolToInchi(mol)
    with open(path + ".inchi", mode="a") as outfile:
        outfile.write(InChi + "\t" + name + "\n")

def delete_old_Inchi_files(*args, **kwargs):
    old_files = [ os.path.join(kwargs["input_dir"], x) for x in os.listdir(kwargs["input_dir"]) if x.endswith(".inchi")]
    for old_file in old_files:
        os.remove(old_file)


def main(args):
    INPUT_DIR = "../../smiles/"

    save_InChi_parser = FileParser(save_InChi, INPUT_DIR, before=delete_old_Inchi_files)
    # save_InChi_parser.run()


if __name__ == "__main__":
    main(sys.argv)


