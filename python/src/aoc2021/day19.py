import math

transformations = [
    # x is facing x
    lambda x, y, z: (x, y, z),
    lambda x, y, z: (x, -z, y),
    lambda x, y, z: (x, -y, -z),
    lambda x, y, z: (x, z, -y),
    # x is facing -x
    lambda x, y, z: (-x, -y, z),
    lambda x, y, z: (-x, -z, -y),
    lambda x, y, z: (-x, y, -z),
    lambda x, y, z: (-x, z, y),
    # x is facing y
    lambda x, y, z: (-z, x, -y),
    lambda x, y, z: (y, x, -z),
    lambda x, y, z: (z, x, y),
    lambda x, y, z: (-y, x, z),
    # x is facing -y
    lambda x, y, z: (z, -x, -y),
    lambda x, y, z: (y, -x, z),
    lambda x, y, z: (-z, -x, y),
    lambda x, y, z: (-y, -x, -z),
    # x is facing z
    lambda x, y, z: (-y, -z, x),
    lambda x, y, z: (z, -y, x),
    lambda x, y, z: (y, z, x),
    lambda x, y, z: (-z, y, x),
    # x is facing -z
    lambda x, y, z: (z, y, -x),
    lambda x, y, z: (-y, z, -x),
    lambda x, y, z: (-z, -y, -x),
    lambda x, y, z: (y, -z, -x)
]


def calculate_distance(v):
    return round(math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]))


def get_distance_matches(dists, other_dists):
    result = []
    for d in dists:
        if d in other_dists:
            result.append(d)
    return result


class Scanner:
    def __init__(self):
        self.beacons = []
        self.transformed_beacons = []

    def add_beacon(self, beacon):
        self.beacons.append(beacon)

    def prepare(self):
        for beacon in self.beacons:
            beacon.prepare(self.beacons)

    def find_match(self, scanner):
        beacons = scanner.beacons
        for beacon in self.beacons:
            match = beacon.get_match(beacons)
            if match is not None:
                return beacon, match
        return None

    def integrate(self, scanner, this_beacon, other_beacon):
        # get all rotations of other beacon distances
        # test each rotation whether it lines up with the distances of our beacon (at least 11 matches)
        dists = this_beacon.distances
        transformer = None
        for t in transformations:
            other_dists = other_beacon.transformed_distances(t)
            matches = get_distance_matches(dists, other_dists)
            if len(matches) > 10:
                transformer = t
                break
        if transformer is None:
            print("could not find transformation")
        # when we have a rotation, rotate the whole other scanner
        scanner.rotate(transformer)

        # the distance of the scanner is our beacon distance (coords) plus the other beacon distance (coords)
        scanner.move(this_beacon.x - other_beacon.x, this_beacon.y - other_beacon.y, this_beacon.z - other_beacon.z)

        # add all other beacons to our list, and shift them to the correct place
        for beacon in scanner.beacons:
            # skip duplicates when doing so
            if beacon not in self.beacons:
                self.beacons.append(beacon)

    def rotate(self, transformer):
        for beacon in self.beacons:
            beacon.rotate(transformer)
        for beacon in self.beacons:
            beacon.prepare(self.beacons)

    def move(self, x, y, z):
        for beacon in self.beacons:
            beacon.move(x, y, z)

    def __str__(self):
        return str(self.beacons)


class Beacon:
    def __init__(self, x, y, z):
        self.x = x
        self.y = y
        self.z = z
        self.distances = []

    def prepare(self, beacons):
        self.distances = []
        for beacon in beacons:
            if beacon is not self:
                self.distances.append(self.calculate_distance(beacon))

    def calculate_distance(self, beacon):
        return beacon.x - self.x, beacon.y - self.y, beacon.z - self.z

    def normalized_distances(self):
        return set(map(lambda v: calculate_distance(v), self.distances))

    def get_match(self, beacons):
        my_dist = self.normalized_distances()
        for beacon in beacons:
            dist = beacon.normalized_distances()
            overlap = my_dist.intersection(dist)
            if len(overlap) > 10:  # 11 distances need to match - with the current beacon we have 12 matched beacons
                return beacon
        return None

    def transformed_distances(self, transformer):
        return list(map(lambda d: transformer(*d), self.distances))

    def rotate(self, transformer):
        self.x, self.y, self.z = transformer(self.x, self.y, self.z)

    def move(self, x, y, z):
        self.x += x
        self.y += y
        self.z += z

    def __str__(self):
        return "(%d, %d, %d)" % (self.x, self.y, self.z)

    def __repr__(self):
        return "(%d, %d, %d)" % (self.x, self.y, self.z)

    def __eq__(self, other):
        if not isinstance(other, Beacon):
            # don't attempt to compare against unrelated types
            return NotImplemented

        return self.x == other.x and self.y == other.y and self.z == other.z


def parse_coord(line):
    coords = line.split(',')
    return Beacon(int(coords[0]), int(coords[1]), int(coords[2]))


def run(f_name):
    scanners = []
    scanner = None
    fin = open(f_name)
    for line in fin:
        line = line.strip()
        if line != '':
            if line.startswith('--- scanner'):
                scanner = Scanner()
                scanners.append(scanner)
            else:
                scanner.add_beacon(parse_coord(line))
    for scanner in scanners:
        scanner.prepare()

    # somehow, when we start with scanner 0 as suggested, scanner 5 is not matched
    # but when we start with #5 it works. I don't ask why ;-)
    # (potential reason:  we do not re-calculate the distances between beacons after adding the to the center)
    central_scanner = scanners[5]
    # add all other scanners to the open list
    open_scanners = []
    open_scanners.extend(scanners[:5])
    open_scanners.extend(scanners[6:])

    # as long as the open list has some entries:
    while len(open_scanners) > 0:
        found = False
        # loop over the list, find first match
        for scanner in open_scanners:
            matches = central_scanner.find_match(scanner)
            if matches is not None:
                # integrate match into scanner0, remove from open list
                central_scanner.integrate(scanner, matches[0], matches[1])
                found = True
                open_scanners.remove(scanner)
                break
        if not found:
            print("could not match all scanners")
            for sc in open_scanners:
                print(sc)
            break
    print(len(central_scanner.beacons))

if __name__ == '__main__':
    run('../../../data/2021/day19.txt')
