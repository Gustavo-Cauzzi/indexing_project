package helpers;

import exceptions.TDEException;
import utils.TdeUtils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

public class PartitionNode {
    private PartitionNode left;
    private PartitionNode right;
    private long partitionInitalAddress;
    private int partitionIndex;
    private RandomAccessFile file;
    private long id;
    private String line;
    private final boolean leaf;
    private long partitionSize;
    private long lineSize;

    public PartitionNode(final long lineSize, final long partitionSize, final List<Long> addresses, final RandomAccessFile f) throws IOException {
        if (addresses.size() == 1) {
            this.leaf = true;
            long address = addresses.get(0);
            this.partitionInitalAddress = address;
            f.seek(address);
            String l = f.readLine();
            this.line = l;
            this.id = TdeUtils.extractId(l);
            this.partitionIndex = 0;
            this.file = f;
            this.partitionSize = partitionSize;
            this.lineSize = lineSize;
        } else {
            this.leaf = false;
            List<List<Long>> partitionedAddresses = TdeUtils.splitInHalf(addresses);
            this.left = new PartitionNode(lineSize, partitionSize, partitionedAddresses.get(0), f);
            this.right = new PartitionNode(lineSize, partitionSize, partitionedAddresses.get(1), f);
        }
    }

    public IndexLineDTO getSmallest () {
        if (this.leaf) {
            return new IndexLineDTO(this.id, this.line);
        } else {
            IndexLineDTO smallestLeft = this.left.getSmallest();
            IndexLineDTO smallestRight = this.right.getSmallest();
            IndexLineDTO smallest = smallestRight.getId() < smallestLeft.getId() ? smallestRight : smallestLeft;
            this.id = smallest.getId();
            this.line = smallest.getLine();
            return smallest;
        }
    }

    public void refreshIf (final long idToRefresh) throws TDEException, IOException {
        if (this.leaf) {
            if (this.id != idToRefresh) {
                throw new TDEException("Folha errada!");
            }

            if (this.partitionIndex + 1 == this.partitionSize) {
                this.id = Long.MAX_VALUE;
            } else {
                this.partitionIndex++;
                this.file.seek(this.partitionInitalAddress + this.partitionIndex * this.lineSize);
                String l = this.file.readLine();
                this.line = l;
                this.id = TdeUtils.extractId(l);
            }
        } else {
            if (this.left.getId() == idToRefresh) {
                this.left.refreshIf(idToRefresh);
                this.left.getSmallest();
            } else if (this.right.getId() == idToRefresh) {
                this.right.refreshIf(idToRefresh);
                this.left.getSmallest();
            } else {
                throw new TDEException("O id para atualizar não está nesse nodo!");
            }
        }
    }

    public long getId() {
        return id;
    }
}
