function [] = dump_simulink (rootBD, xmlFileName)
% Dumps the contents of the current simulink project to an XML file

docNode = com.mathworks.xml.XMLUtils.createDocument('simulink-dump');
docRootNode = docNode.getDocumentElement;

blocks = find_system (rootBD, 'Type', 'block');

for i = 1:size(blocks)
    block = blocks(i);
    blockNode = docNode.createElement('block');
    blockNode.setAttribute('name', get_param(block,'Name'));
    blockNode.setAttribute('parent', get_param(block,'Parent'));
    blockNode.setAttribute('type', get_param (block, 'BlockType'));
    
    portHandles = get_param (block, 'PortHandles');
    isz = size(portHandles{1}.Inport);
    blockNode.setAttribute('inports', int2str(isz(2)));
    osz = size(portHandles{1}.Outport);
    blockNode.setAttribute('outports', int2str(osz(2)));
    docRootNode.appendChild(blockNode);
    
    dialogParams = get_param (block, 'DialogParameters');
    fn = fieldnames(dialogParams{1});
    for j = 1:size(fn)
        diaNode = docNode.createElement('dialog');
        diaNode.setAttribute('param', fn(j));
        fname = fn(j);
        value = get_param (block, fname{1});
        diaNode.setAttribute('value',  value);
        blockNode.appendChild (diaNode);
    end
end

lines = find_system (rootBD, 'FindAll', 'on', 'Type', 'line');

for i = 1:size(lines)
    line = lines(i);
    srcPort = get_param (line, 'SrcPortHandle');
    dstPort = get_param (line, 'DstPortHandle');
    
    dstParents = get_param(dstPort,'Parent'); 
    dstPortNums = get_param(dstPort,'PortNumber');
    
    if iscell (dstParents)
        % skip these, as they are always also given as single entries
    else
        lineNode = docNode.createElement('line');
        lineNode.setAttribute('src', ...
            strcat (get_param(srcPort,'Parent'), ':', ...
                int2str(get_param(srcPort,'PortNumber'))));
        lineNode.setAttribute('dest', ...
            strcat (dstParents, ':', int2str(dstPortNums)));
        docRootNode.appendChild(lineNode);
    end
end


xmlwrite(xmlFileName, docNode);


