import re

# pattern for a line
pattern = r'x=(\-?\d+)\.\.(\-?\d+),y=(\-?\d+)\.\.(\-?\d+),z=(\-?\d+)\.\.(\-?\d+)'


class Region:
    def __init__(self, x_start, x_end, y_start, y_end, z_start, z_end, sign):
        self.z_end = z_end
        self.z_start = z_start
        self.y_end = y_end
        self.y_start = y_start
        self.x_end = x_end
        self.x_start = x_start
        self.sign = sign

    def overlaps_any(self, regions):
        for r in regions:
            if r is not self:
                if self.overlaps(r):
                    return True
        return False

    # there is no overlap if for any axis there is no overlap
    def overlaps(self, region):
        ix0 = max(self.x_start, region.x_start)
        ix1 = min(self.x_end, region.x_end)
        iy0 = max(self.y_start, region.y_start)
        iy1 = min(self.y_end, region.y_end)
        iz0 = max(self.z_start, region.z_start)
        iz1 = min(self.z_end, region.z_end)
        if ix0 <= ix1 and iy0 <= iy1 and iz0 <= iz1:
            return True
        return False

    def get_intersect(self, region):
        ix0 = max(self.x_start, region.x_start)
        ix1 = min(self.x_end, region.x_end)
        iy0 = max(self.y_start, region.y_start)
        iy1 = min(self.y_end, region.y_end)
        iz0 = max(self.z_start, region.z_start)
        iz1 = min(self.z_end, region.z_end)
        if ix0 <= ix1 and iy0 <= iy1 and iz0 <= iz1:
            return Region(ix0, ix1, iy0, iy1, iz0, iz1, -region.sign)

    # true when the other region is fully in the current one
    def contains_fully(self, region):
        return self.x_start <= region.x_start <= region.x_end < self.x_end and \
               self.y_start <= region.y_start <= region.y_end < self.y_end and \
               self.z_start <= region.z_start <= region.z_end < self.z_end

    def __repr__(self):
        return "(%d..%d, %d..%d, %d..%d)=%d" % \
               (self.x_start, self.x_end, self.y_start, self.y_end, self.z_start, self.z_end, self.sign)

    def value(self):
        return self.sign * \
               (self.x_end - self.x_start + 1) * \
               (self.y_end - self.y_start + 1) * \
               (self.z_end - self.z_start + 1)


def parse(line):
    if line.startswith('on'):
        line = line[3:]
        value = 1
    else:
        line = line[4:]
        value = -1
    match = re.search(pattern, line)

    return Region(int(match.group(1)), int(match.group(2)),
                  int(match.group(3)), int(match.group(4)),
                  int(match.group(5)), int(match.group(6)),
                  value)


def run(f_name):
    fin = open(f_name)

    regions = []
    for line in fin:
        line = line.strip()
        if line == '':
            break
        regions.append(parse(line))

    # discard any regions which are contained fully in a later region
    # (they will not do anything)
    contained = []
    for i, r in enumerate(regions):
        for other in regions[i:]:
            if other.contains_fully(r):
                contained.append(r)
    print("contained regions:", contained)

    # single out all region which do not overlap anything else, and are not contained in an earlier region
    # keep the 'on' regions, and discard the 'off' regions (they do not do anything)
    singles = []
    for r in regions:
        if not r.overlaps_any(regions):
            singles.append(r)

    # looks as if we got just one 'off' region
    print("non-overlapping regions", singles)

    # go through all regions in order
    #   for any overlap with an existing region, store a new overlap region with the opposing sign
    #   if its an 'on' region, store it
    known = []
    for region in regions:
        overlaps = []
        for k in known:
            if region.overlaps(k):
                overlap = region.get_intersect(k)
                overlaps.append(overlap)
        known.extend(overlaps)
        if region.sign == 1:
            known.append(region)

    # when all steps are finished, run through all regions and count their cubes

    result = 0
    for r in known:
        result += r.value()
    # result for test data: 2758514936282235
    print(result)


if __name__ == '__main__':
    run('../data/day22.txt')
