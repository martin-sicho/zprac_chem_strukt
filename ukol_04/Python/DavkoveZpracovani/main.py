import sys, os
from rdkit import Chem
from FileParser import FileParser

from rdkit.Chem import Descriptors

# 1 #

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

# 2 #

def save_mols_with_nitrile(mol, **kwargs):
    weight = Descriptors.ExactMolWt(mol)
    if weight < 150.0:
        patt = Chem.MolFromSmarts('[NX1]#[CX2]')
        if mol.HasSubstructMatch(patt):
            return mol

def save_mols_with_nitrile_ret_function(mol):
    if mol:
        with open("mols_with_nitrile_group.smi", mode="a") as outfile:
            outfile.write(Chem.MolToSmiles(mol) + "\n")

def delete_old_outfiles(*args, **kwargs):
    old_files = [ x for x in os.listdir(".") if x.endswith(".smi")]
    for old_file in old_files:
        os.remove(old_file)

# 3 #

def getMoleculesMolWeightLessThan(mol, **kwargs):
    threshold = getMoleculesMolWeightLessThan.args[0]
    if Descriptors.ExactMolWt(mol) < threshold:
        return mol

def appendToSet(mol):
    if mol:
        appendToSet.set.add(mol)
appendToSet.set = set()

# 4 # FIXME: do this correctly

# def appendToAcceptedSet(mol, **kwargs):
#     accepted = appendToAcceptedSet.args[0]
#     atom_count = mol.GetNumHeavyAtoms()
#     if atom_count <= 4:
#         if not accepted:
#             accepted.add(mol)
#             return
#
#         to_remove = set()
#         found_superstructure = False
#         for item in accepted:
#             if mol.HasSubstructMatch(item):
#                 to_remove.add(item)
#             if item.HasSubstructMatch(mol):
#                 found_superstructure = True
#
#         accepted.difference_update(to_remove)
#         if not found_superstructure:
#             accepted.add(mol)
#     else:
#         to_remove = set()
#         for item in accepted:
#             if mol.HasSubstructMatch(item):
#                 to_remove.add(item)
#         accepted.difference_update(to_remove)

def main(args):
    INPUT_DIR = "../../smiles/"

    # 1 #
    save_InChi_parser = FileParser(save_InChi, INPUT_DIR, before=delete_old_Inchi_files)
    # save_InChi_parser.run()

    # 2 #
    save_some_mols_parser = FileParser(save_mols_with_nitrile, INPUT_DIR, ret_function=save_mols_with_nitrile_ret_function, before=delete_old_outfiles)
    # save_some_mols_parser.run()

    # 3 #
    return_set = set()
    filter_method_parser = FileParser(getMoleculesMolWeightLessThan, INPUT_DIR, args=[150], ret_function=appendToSet)
    appendToSet.set = return_set
    # filter_method_parser.run()

    # 4 #
    # accepted_set = set()
    # filter_method_parser = FileParser(appendToAcceptedSet, INPUT_DIR, args=[accepted_set])
    # filter_method_parser.run()

if __name__ == "__main__":
    main(sys.argv)


